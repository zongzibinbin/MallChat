package com.abin.mallchat.custom.common.handshake;

import com.abin.mallchat.common.common.domain.vo.response.ApiResult;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Predicate;

/**
 * <p>
 *  WS握手时鉴权
 * </p>
 *
 * @Title: HandshakeHandler
 * @Author Macro Chen
 * @PACKAGE com.abin.mallchat.custom.common.handshake.HandshakeHandler
 * @Date 2023-06-28 22:05:40
 */
@Slf4j
@ChannelHandler.Sharable
public class HandshakeHandler extends ChannelInboundHandlerAdapter {

    /**
     * 鉴权逻辑
     *
     * @Author Macro Chen
     * @see Predicate<HandshakeEvent>
     */
    private final Predicate<HandshakeEvent> handshakePredicate;

    public HandshakeHandler(Predicate<HandshakeEvent> handshakePredicate) {
        this.handshakePredicate = handshakePredicate;
    }

    /**
     * 握手认证
     *
     * @param ctx ctx
     * @param evt evt
     * @Name: userEventTriggered
     * @Author Macro Chen
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {

        super.userEventTriggered(ctx, evt);

        if (evt instanceof WebSocketServerProtocolHandler.HandshakeComplete) {
            doAuthentication(ctx, (WebSocketServerProtocolHandler.HandshakeComplete) evt);
        }

    }

    /**
     * 认证逻辑
     *
     * @param context 上下文
     * @param event   事件
     * @Name: doAuthentication
     * @Author Macro Chen
     */
    private void doAuthentication(ChannelHandlerContext context, WebSocketServerProtocolHandler.HandshakeComplete event) {
        if (handshakePredicate == null) {
            log.warn("not found handshakePredicate component");
            return;
        }
        /*
         * 鉴权不通过，向客户端发送响应体并关闭链接
         */
        if (!handshakePredicate.test(HandshakeEvent.of(event))) {
            context.channel().writeAndFlush(ApiResult.unauthorized()).addListener(ChannelFutureListener.CLOSE);
        }
    }
}