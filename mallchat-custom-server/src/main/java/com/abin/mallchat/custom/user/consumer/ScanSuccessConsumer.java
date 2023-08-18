package com.abin.mallchat.custom.user.consumer;

import cn.hutool.json.JSONUtil;
import com.abin.mallchat.common.common.constant.MQConstant;
import com.abin.mallchat.common.common.domain.dto.ScanSuccessMessageDTO;
import com.abin.mallchat.common.common.utils.CacheHolder;
import com.abin.mallchat.common.user.dao.UserDao;
import com.abin.mallchat.common.user.domain.entity.User;
import com.abin.mallchat.custom.user.service.WebSocketService;
import com.abin.mallchat.custom.user.service.WxMsgService;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * Description: 将扫码成功的信息发送给对应的用户,等待授权
 * Author: <a href="https://github.com/zongzibinbin">abin</a>
 * Date: 2023-08-12
 */
@RocketMQMessageListener(consumerGroup = MQConstant.SCAN_MSG_GROUP, topic = MQConstant.SCAN_MSG_TOPIC, messageModel = MessageModel.BROADCASTING)
@Component
public class ScanSuccessConsumer implements RocketMQListener<ScanSuccessMessageDTO> {
    @Autowired
    private WebSocketService webSocketService;
    @Autowired
    private WxMsgService wxMsgService;
    @Autowired
    private UserDao userDao;

    @Override
    public void onMessage(ScanSuccessMessageDTO scanSuccessMessageDTO) {
        Integer loginCode = scanSuccessMessageDTO.getLoginCode();
        //本地未储存对应的channel,则结束
        Channel channel = CacheHolder.WAIT_LOGIN_MAP.getIfPresent(loginCode);
        if (Objects.isNull(channel)) {
            return;
        }
        //给正在等待登陆的channel发送扫码成功的消息，等待授权
        channel.writeAndFlush(new TextWebSocketFrame(JSONUtil.toJsonStr(scanSuccessMessageDTO.getWsBaseMsg())));
    }


}
