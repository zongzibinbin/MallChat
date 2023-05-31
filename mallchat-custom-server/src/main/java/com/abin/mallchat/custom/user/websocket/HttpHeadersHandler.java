package com.abin.mallchat.custom.user.websocket;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.util.AttributeKey;

import java.net.InetSocketAddress;
import java.util.Objects;

/**
 * <p>
 * Http 请求头处理
 * </p>
 *
 * @author <a href="https://github.com/zongzibinbin">abin</a>
 * @since 2023-03-22
 */
public class HttpHeadersHandler extends ChannelInboundHandlerAdapter {
    private AttributeKey<String> key = AttributeKey.valueOf("Id");

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof FullHttpRequest) {
            HttpHeaders headers = ((FullHttpRequest) msg).headers();
            String ip = headers.get("X-Real-IP");
            //如果没经过nginx，就直接获取远端地址
            if (Objects.isNull(ip)) {
                InetSocketAddress address = (InetSocketAddress) ctx.channel().remoteAddress();
                ip = address.getAddress().getHostAddress();
            }
            NettyUtil.setAttr(ctx.channel(), NettyUtil.IP, ip);
        }
        ctx.fireChannelRead(msg);
    }

}