package com.abin.mallchat.custom.user.websocket;


import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakeException;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolConfig;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.ssl.SslHandler;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;
import io.netty.util.internal.ObjectUtil;

import java.util.concurrent.TimeUnit;

public class WebSocketHandshakeHandler extends ChannelInboundHandlerAdapter {

    private final WebSocketServerProtocolConfig serverConfig;
    private ChannelHandlerContext ctx;
    private ChannelPromise handshakePromise;
    private boolean isWebSocketPath;

    public WebSocketHandshakeHandler(WebSocketServerProtocolConfig serverConfig) {
        this.serverConfig = (WebSocketServerProtocolConfig)ObjectUtil.checkNotNull(serverConfig, "serverConfig");
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) {
        this.ctx = ctx;
        this.handshakePromise = ctx.newPromise();
    }

    @Override
    public void channelRead(final ChannelHandlerContext ctx, Object msg) throws Exception {
        HttpObject httpObject = (HttpObject)msg;
        if (httpObject instanceof HttpRequest) {
            final HttpRequest req = (HttpRequest)httpObject;
            this.isWebSocketPath = this.isWebSocketPath(req);
            if (!this.isWebSocketPath) {
                ctx.fireChannelRead(msg);
                return;
            }
            try {
                if (HttpMethod.GET.equals(req.method())) {
                    final String token = req.headers().get("Sec-Websocket-Protocol");
                    WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(getWebSocketLocation(ctx.pipeline(), req, this.serverConfig.websocketPath()), token, this.serverConfig.decoderConfig());
                    final WebSocketServerHandshaker handshaker = wsFactory.newHandshaker(req);
                    NettyUtil.setAttr(ctx.channel(),NettyUtil.HANDSHAKER_ATTR_KEY,handshaker);
                    final ChannelPromise localHandshakePromise = this.handshakePromise;
                    if (handshaker == null) {
                        WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
                    } else {

                        ctx.pipeline().remove(this);
                        ChannelFuture handshakeFuture = handshaker.handshake(ctx.channel(), req);
                        handshakeFuture.addListener(new ChannelFutureListener() {
                            @Override
                            public void operationComplete(ChannelFuture future) {
                                if (!future.isSuccess()) {
                                    localHandshakePromise.tryFailure(future.cause());
                                    ctx.fireExceptionCaught(future.cause());
                                } else {
                                    localHandshakePromise.trySuccess();
                                    NettyUtil.setAttr(ctx.channel(), NettyUtil.TOKEN, token);
                                    ctx.fireUserEventTriggered(WebSocketServerProtocolHandler.ServerHandshakeStateEvent.HANDSHAKE_COMPLETE);
                                }
                            }
                        });
                        this.applyHandshakeTimeout();
                    }

                    return;
                }

                sendHttpResponse(ctx, req, new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.FORBIDDEN, ctx.alloc().buffer(0)));
            } finally {
                ReferenceCountUtil.release(req);
            }

            return;
        } else if (!this.isWebSocketPath) {
            ctx.fireChannelRead(msg);
        } else {
            ReferenceCountUtil.release(msg);
        }

    }

    private boolean isWebSocketPath(HttpRequest req) {
        String websocketPath = this.serverConfig.websocketPath();
        String uri = req.uri();
        boolean checkStartUri = uri.startsWith(websocketPath);
        boolean checkNextUri = "/".equals(websocketPath) || this.checkNextUri(uri, websocketPath);
        return this.serverConfig.checkStartsWith() ? checkStartUri && checkNextUri : uri.equals(websocketPath);
    }

    private boolean checkNextUri(String uri, String websocketPath) {
        int len = websocketPath.length();
        if (uri.length() <= len) {
            return true;
        } else {
            char nextUri = uri.charAt(len);
            return nextUri == '/' || nextUri == '?';
        }
    }

    private static void sendHttpResponse(ChannelHandlerContext ctx, HttpRequest req, HttpResponse res) {
        ChannelFuture f = ctx.channel().writeAndFlush(res);
        if (!HttpUtil.isKeepAlive(req) || res.status().code() != 200) {
            f.addListener(ChannelFutureListener.CLOSE);
        }

    }

    private static String getWebSocketLocation(ChannelPipeline cp, HttpRequest req, String path) {
        String protocol = "ws";
        if (cp.get(SslHandler.class) != null) {
            protocol = "wss";
        }

        String host = req.headers().get(HttpHeaderNames.HOST);
        return protocol + "://" + host + path;
    }

    private void applyHandshakeTimeout() {
        final ChannelPromise localHandshakePromise = this.handshakePromise;
        long handshakeTimeoutMillis = this.serverConfig.handshakeTimeoutMillis();
        if (handshakeTimeoutMillis > 0L && !localHandshakePromise.isDone()) {
            final Future<?> timeoutFuture = this.ctx.executor().schedule(new Runnable() {
                @Override
                public void run() {
                    if (!localHandshakePromise.isDone() && localHandshakePromise.tryFailure(new WebSocketServerHandshakeException("handshake timed out"))) {
                        WebSocketHandshakeHandler.this.ctx.flush().fireUserEventTriggered(WebSocketServerProtocolHandler.ServerHandshakeStateEvent.HANDSHAKE_TIMEOUT).close();
                    }

                }
            }, handshakeTimeoutMillis, TimeUnit.MILLISECONDS);
            localHandshakePromise.addListener(new FutureListener<Void>() {
                @Override
                public void operationComplete(Future<Void> f) {
                    timeoutFuture.cancel(false);
                }
            });
        }
    }

}
