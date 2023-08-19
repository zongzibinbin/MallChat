package com.abin.mallchat.custom.user.consumer;

import com.abin.mallchat.common.common.constant.MQConstant;
import com.abin.mallchat.common.common.constant.RedisKey;
import com.abin.mallchat.common.common.domain.dto.LoginMessageDTO;
import com.abin.mallchat.common.common.utils.CacheHolder;
import com.abin.mallchat.common.user.dao.UserDao;
import com.abin.mallchat.common.user.domain.entity.User;
import com.abin.mallchat.custom.user.service.WebSocketService;
import com.abin.mallchat.custom.user.service.WxMsgService;
import io.netty.channel.Channel;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * Description: 在本地服务上找寻对应channel，将对应用户登陆，并触发所有用户收到上线事件
 * Author: <a href="https://github.com/zongzibinbin">abin</a>
 * Date: 2023-08-12
 */
@RocketMQMessageListener(consumerGroup = MQConstant.LOGIN_MSG_GROUP, topic = MQConstant.LOGIN_MSG_TOPIC, messageModel = MessageModel.BROADCASTING)
@Component
public class MsgLoginConsumer implements RocketMQListener<LoginMessageDTO> {
    @Autowired
    private WxMsgService wxMsgService;
    @Autowired
    private UserDao userDao;

    @Override
    public void onMessage(LoginMessageDTO loginMessageDTO) {
        WxMpXmlMessage wxMpXmlMessage = loginMessageDTO.getWxMpXmlMessage();
        //给二维码绑定的登录code
        Integer eventKey = Integer.parseInt(getEventKey(wxMpXmlMessage));
        //本地未储存对应的channel,则结束
        Channel channel = CacheHolder.WAIT_LOGIN_MAP.getIfPresent(eventKey);
        if (Objects.isNull(channel)) {
            return;
        }
        //查询openid对应的用户(必然存在)
        String openid = wxMpXmlMessage.getFromUser();
        User user = userDao.getByOpenId(openid);
        //登录,并且清除缓存
        wxMsgService.login(user.getId(), eventKey);
    }

    private String getEventKey(WxMpXmlMessage wxMpXmlMessage) {
        //扫码关注的渠道事件有前缀，需要去除
        return wxMpXmlMessage.getEventKey().replace("qrscene_", "");
    }

}
