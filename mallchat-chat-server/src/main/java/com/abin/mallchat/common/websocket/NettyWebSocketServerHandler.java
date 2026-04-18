package com.abin.mallchat.common.websocket;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import com.abin.mallchat.common.common.constant.MDCKey;
import com.abin.mallchat.common.user.domain.enums.WSReqTypeEnum;
import com.abin.mallchat.common.user.domain.vo.request.ws.WSAuthorize;
import com.abin.mallchat.common.user.domain.vo.request.ws.WSBaseReq;
import com.abin.mallchat.common.user.service.WebSocketService;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;

import java.util.Objects;


@Slf4j
@Sharable
public class NettyWebSocketServerHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    private WebSocketService webSocketService;

    private void setMDCContext(ChannelHandlerContext ctx) {
        String tid = NettyUtil.getAttr(ctx.channel(), NettyUtil.TID);
        if (Objects.nonNull(tid)) {
            MDC.put(MDCKey.TID, tid);
        }
        Long uid = NettyUtil.getAttr(ctx.channel(), NettyUtil.UID);
        if (Objects.nonNull(uid)) {
            MDC.put(MDCKey.UID, String.valueOf(uid));
        }
    }

    private void clearMDCContext() {
        MDC.remove(MDCKey.TID);
        MDC.remove(MDCKey.UID);
    }

    // 当web客户端连接后，触发该方法
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        try {
            setMDCContext(ctx);
            this.webSocketService = getService();
        } finally {
            clearMDCContext();
        }
    }

    // 客户端离线
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        try {
            setMDCContext(ctx);
            userOffLine(ctx);
        } finally {
            clearMDCContext();
        }
    }

    /**
     * 取消绑定
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        try {
            setMDCContext(ctx);
            // 可能出现业务判断离线后再次触发 channelInactive
            log.warn("触发 channelInactive 掉线![{}]", ctx.channel().id());
            userOffLine(ctx);
        } finally {
            clearMDCContext();
        }
    }

    private void userOffLine(ChannelHandlerContext ctx) {
        this.webSocketService.removed(ctx.channel());
        ctx.channel().close();
    }

    /**
     * 心跳检查
     *
     * @param ctx
     * @param evt
     * @throws Exception
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        try {
            setMDCContext(ctx);
            if (evt instanceof IdleStateEvent) {
                IdleStateEvent idleStateEvent = (IdleStateEvent) evt;
                // 读空闲
                if (idleStateEvent.state() == IdleState.READER_IDLE) {
                    // 关闭用户的连接
                    userOffLine(ctx);
                }
            } else if (evt instanceof WebSocketServerProtocolHandler.HandshakeComplete) {
                this.webSocketService.connect(ctx.channel());
                String token = NettyUtil.getAttr(ctx.channel(), NettyUtil.TOKEN);
                if (StrUtil.isNotBlank(token)) {
                    this.webSocketService.authorize(ctx.channel(), new WSAuthorize(token));
                }
            }
            super.userEventTriggered(ctx, evt);
        } finally {
            clearMDCContext();
        }
    }

    // 处理异常
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        try {
            setMDCContext(ctx);
            log.warn("异常发生，异常消息 ={}", cause);
            ctx.channel().close();
        } finally {
            clearMDCContext();
        }
    }

    private WebSocketService getService() {
        return SpringUtil.getBean(WebSocketService.class);
    }

    // 读取客户端发送的请求报文
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        try {
            setMDCContext(ctx);
            WSBaseReq wsBaseReq = JSONUtil.toBean(msg.text(), WSBaseReq.class);
            WSReqTypeEnum wsReqTypeEnum = WSReqTypeEnum.of(wsBaseReq.getType());
            switch (wsReqTypeEnum) {
                case LOGIN:
                    this.webSocketService.handleLoginReq(ctx.channel());
                    log.info("请求二维码 = " + msg.text());
                    break;
                case HEARTBEAT:
                    break;
                default:
                    log.info("未知类型");
            }
        } finally {
            clearMDCContext();
        }
    }
}
