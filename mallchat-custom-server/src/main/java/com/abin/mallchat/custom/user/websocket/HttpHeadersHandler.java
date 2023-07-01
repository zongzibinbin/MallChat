package com.abin.mallchat.custom.user.websocket;

import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.util.AttributeKey;
import org.apache.commons.lang3.StringUtils;

import java.net.InetSocketAddress;

public class HttpHeadersHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof FullHttpRequest) {
            FullHttpRequest request = (FullHttpRequest) msg;
            HttpHeaders headers = request.headers();
            String ip = headers.get("X-Real-IP");
            if (StringUtils.isEmpty(ip)) {//如果没经过nginx，就直接获取远端地址
                InetSocketAddress address = (InetSocketAddress) ctx.channel().remoteAddress();
                ip = address.getAddress().getHostAddress();
            }

            String[] uriSplit = request.uri().split("[?]");
            FullHttpRequest modifiedRequest =  new DefaultFullHttpRequest(request.protocolVersion(), request.method(), uriSplit[0]);
            modifiedRequest.headers().add(headers);
            modifiedRequest.content().writeBytes(request.content());


            NettyUtil.setAttr(ctx.channel(), NettyUtil.IP, ip);
            if (uriSplit.length>=2) {
                NettyUtil.setAttr(ctx.channel(), NettyUtil.TOKEN, uriSplit[1]);
            }
            ctx.fireChannelRead(modifiedRequest);
        }else {
            super.channelRead(ctx, msg);
        }
    }
}