package com.abin.mallchat.custom.chat.service.impl;

import cn.hutool.core.thread.NamedThreadFactory;
import com.abin.mallchat.common.common.handler.GlobalUncaughtExceptionHandler;
import com.abin.mallchat.common.common.utils.JsonUtils;
import com.abin.mallchat.common.user.domain.entity.User;
import com.abin.mallchat.common.user.service.cache.UserCache;
import com.abin.mallchat.custom.chat.service.WeChatMsgOperationService;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.api.WxMpTemplateMsgService;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateData;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class WeChatMsgOperationServiceImpl  implements WeChatMsgOperationService {

    private static final ExecutorService executor = new ThreadPoolExecutor(1, 10, 3000L,
            TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<Runnable>(20),
            new NamedThreadFactory("wechat-operation-thread", null, false,
                    new GlobalUncaughtExceptionHandler()));

    // at消息的微信推送模板id
    private final String atMsgPublishTemplateId = "";

    private final String WE_CHAT_MSG_COLOR = "#A349A4";

    @Autowired
    private UserCache userCache;

    @Autowired
    WxMpService wxMpService;

    @Override
    public void publishChatMsgToWeChatUser(long senderUid, List<Long> receiverUidList, String msg) {
        User sender = userCache.getUserInfo(senderUid);
        Set uidSet = new HashSet();
        uidSet.addAll(receiverUidList);
        Map<Long, User> userMap = userCache.getUserInfoBatch(uidSet);
        userMap.values().forEach(user -> {
            if (Objects.nonNull(user.getOpenId()) && user.isPublishChatToWechatSwitch()) {
                executor.execute(() -> {
                    WxMpTemplateMessage msgTemplate = getAtMsgTemplate(sender, user.getOpenId(), msg);
                    publishTemplateMsg(msgTemplate);
                });
            }
        });
    }

    /*
     * 构造微信模板消息
     */
    private WxMpTemplateMessage getAtMsgTemplate(User sender, String openId, String msg) {
        return WxMpTemplateMessage.builder()
                .toUser(openId)
                .templateId(atMsgPublishTemplateId)
                .data(generateAtMsgData(sender, msg))
                .build();
    }

    /*
     * 构造微信消息模板的数据
     */
    private List<WxMpTemplateData> generateAtMsgData(User sender, String msg) {
        List dataList = new ArrayList<WxMpTemplateData>();
//        todo: 没有消息模板，暂不实现
//        dataList.add(new WxMpTemplateData("senderName", sender.getName() , WE_CHAT_MSG_COLOR));
//        dataList.add(new WxMpTemplateData("content", msg , WE_CHAT_MSG_COLOR));
        return dataList;
    }

    /**
     * 推送微信模板消息
     *
     * @param templateMsg 微信模板消息
     */
    protected void publishTemplateMsg(WxMpTemplateMessage templateMsg) {
        WxMpTemplateMsgService wxMpTemplateMsgService = wxMpService.getTemplateMsgService();
        try {
            wxMpTemplateMsgService.sendTemplateMsg(templateMsg);
        } catch (WxErrorException e) {
            log.error("publish we chat msg failed! open id is {}, msg is {}.",
                    templateMsg.getToUser(), JsonUtils.toStr(templateMsg.getData()));
        }
    }
}
