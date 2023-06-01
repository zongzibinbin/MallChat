package com.abin.mallchat.custom.user.service;

import com.abin.mallchat.common.user.domain.entity.User;
import com.abin.mallchat.custom.user.domain.vo.request.ws.WSAuthorize;
import com.abin.mallchat.custom.user.domain.vo.response.ws.WSBaseResp;
import io.netty.channel.Channel;

public interface WebSocketService {
    /**
     * 处理用户登录请求，需要返回一张带code的二维码
     *
     * @param channel
     */
    void handleLoginReq(Channel channel);

    /**
     * 处理所有ws连接的事件
     *
     * @param channel
     */
    void connect(Channel channel);

    /**
     * 处理ws断开连接的事件
     *
     * @param channel
     */
    void removed(Channel channel);

    /**
     * 主动认证登录
     *
     * @param channel
     * @param wsAuthorize
     */
    void authorize(Channel channel, WSAuthorize wsAuthorize);

    /**
     * 扫码用户登录成功通知
     *
     * @param loginCode
     * @param user
     * @param token
     */
    Boolean scanLoginSuccess(Integer loginCode, User user, String token);

    /**
     * 用户扫码成功
     *
     * @param loginCode
     */
    Boolean scanSuccess(Integer loginCode);

    /**
     * 推动消息给所有在线的人
     *
     * @param wsBaseResp 发送的消息体
     * @param skipUid    需要跳过的人
     */
    void sendToAllOnline(WSBaseResp<?> wsBaseResp, Long skipUid);

    /**
     * 推动消息给所有在线的人
     *
     * @param wsBaseResp 发送的消息体
     */
    void sendToAllOnline(WSBaseResp<?> wsBaseResp);


}
