package com.abin.mallchat.custom.user.websocket;


import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;

public class WebSocketHandshakeHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof FullHttpRequest) {
            FullHttpRequest request = (FullHttpRequest) msg;
            String token = request.headers().get("Sec-Websocket-Protocol");
            NettyUtil.setAttr(ctx.channel(), NettyUtil.TOKEN, token);
            // 构建WebSocket握手处理器
            WebSocketServerHandshakerFactory handshakeFactory = new WebSocketServerHandshakerFactory(
                    request.uri(), token, false);
            WebSocketServerHandshaker handshake = handshakeFactory.newHandshaker(request);
            final ChannelFuture handshakeFuture = handshake.handshake(ctx.channel(), request);
            ctx.pipeline().remove(this);
            handshakeFuture.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) {
                    if (!future.isSuccess()) {
                        ctx.fireExceptionCaught(future.cause());
                    } else {
                        // 手动触发WebSocket握手状态事件
                        ctx.fireUserEventTriggered(
                                WebSocketServerProtocolHandler.ServerHandshakeStateEvent.HANDSHAKE_COMPLETE);
                    }
                }
            });
        } else {
            super.channelRead(ctx, msg);
        }
    }
}
