package com.abin.mallchat.custom.user.websocket;


import cn.hutool.core.util.ReflectUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.Utf8FrameValidator;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolConfig;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import lombok.SneakyThrows;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

public class NettyWebSocketServerProtocolHandler extends WebSocketServerProtocolHandler {

    private WebSocketServerProtocolConfig webSocketServerProtocolConfig;
    public NettyWebSocketServerProtocolHandler(String websocketPath) {
        super(websocketPath);
    }

    @Override
    @SneakyThrows
    public void handlerAdded(ChannelHandlerContext ctx) {
        Field serverConfig = ReflectUtil.getField(super.getClass(), "serverConfig");
        serverConfig.setAccessible(true);
        this.webSocketServerProtocolConfig = (WebSocketServerProtocolConfig)serverConfig.get(this);

        ChannelPipeline cp = ctx.pipeline();
        if (cp.get(WebSocketHandshakeHandler.class) == null) {
            cp.addBefore(ctx.name(), WebSocketHandshakeHandler.class.getName(), new WebSocketHandshakeHandler(this.webSocketServerProtocolConfig));
        }

        if (this.webSocketServerProtocolConfig.decoderConfig().withUTF8Validator() && cp.get(Utf8FrameValidator.class) == null) {
            cp.addBefore(ctx.name(), Utf8FrameValidator.class.getName(), new Utf8FrameValidator(this.webSocketServerProtocolConfig.decoderConfig().closeOnProtocolViolation()));
        }

    }

    @Override
    @SneakyThrows
    protected void decode(ChannelHandlerContext ctx, WebSocketFrame frame, List<Object> out) throws Exception {
        if (this.webSocketServerProtocolConfig.handleCloseFrames() && frame instanceof CloseWebSocketFrame) {
            WebSocketServerHandshaker handshaker = NettyUtil.getAttr(ctx.channel(),NettyUtil.HANDSHAKER_ATTR_KEY);
            if (handshaker != null) {
                frame.retain();
                ChannelPromise promise = ctx.newPromise();
                Method closeSent = ReflectUtil.getMethod(super.getClass(), "closeSent", ChannelPromise.class);
                closeSent.setAccessible(true);
                closeSent.invoke(this,promise);
                handshaker.close(ctx, (CloseWebSocketFrame)frame, promise);
            } else {
                ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
            }
        } else {
            super.decode(ctx, frame, out);
        }
    }
}
