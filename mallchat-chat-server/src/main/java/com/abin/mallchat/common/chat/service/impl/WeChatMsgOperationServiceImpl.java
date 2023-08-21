package com.abin.mallchat.common.chat.service.impl;

import cn.hutool.core.thread.NamedThreadFactory;
import com.abin.mallchat.common.chat.service.WeChatMsgOperationService;
import com.abin.mallchat.common.common.domain.dto.FrequencyControlDTO;
import com.abin.mallchat.common.common.exception.FrequencyControlException;
import com.abin.mallchat.common.common.handler.GlobalUncaughtExceptionHandler;
import com.abin.mallchat.common.common.service.frequencycontrol.FrequencyControlUtil;
import com.abin.mallchat.common.user.domain.entity.User;
import com.abin.mallchat.common.user.service.cache.UserCache;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateData;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static com.abin.mallchat.common.common.service.frequencycontrol.FrequencyControlStrategyFactory.TOTAL_COUNT_WITH_IN_FIX_TIME_FREQUENCY_CONTROLLER;

@Slf4j
@Component
public class WeChatMsgOperationServiceImpl implements WeChatMsgOperationService {

    private static final ExecutorService executor = new ThreadPoolExecutor(1, 10, 3000L,
            TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<Runnable>(20),
            new NamedThreadFactory("wechat-operation-thread", null, false,
                    GlobalUncaughtExceptionHandler.getInstance()));

    // at消息的微信推送模板id
    private final String atMsgPublishTemplateId = "Xd7sWPZsuWa0UmpvLaZPvaJVjNj1KjEa0zLOm5_Z7IU";

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
            if (Objects.nonNull(user.getOpenId())) {
                executor.execute(() -> {
                    WxMpTemplateMessage msgTemplate = getAtMsgTemplate(sender, user.getOpenId(), msg);
                    publishTemplateMsgCheckLimit(msgTemplate);
                });
            }
        });
    }

    private void publishTemplateMsgCheckLimit(WxMpTemplateMessage msgTemplate) {
        try {
            FrequencyControlDTO frequencyControlDTO = new FrequencyControlDTO();
            frequencyControlDTO.setKey("TemplateMsg:" + msgTemplate.getToUser());
            frequencyControlDTO.setUnit(TimeUnit.HOURS);
            frequencyControlDTO.setCount(1);
            frequencyControlDTO.setTime(1);
            FrequencyControlUtil.executeWithFrequencyControl(TOTAL_COUNT_WITH_IN_FIX_TIME_FREQUENCY_CONTROLLER, frequencyControlDTO,
                    () -> publishTemplateMsg(msgTemplate));
        } catch (FrequencyControlException e) {
            log.info("wx push limit openid:{}", msgTemplate.getToUser());
        } catch (Throwable e) {
            log.error("wx push error openid:{}", msgTemplate.getToUser());
        }
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
        dataList.add(new WxMpTemplateData("name", sender.getName(), WE_CHAT_MSG_COLOR));
        dataList.add(new WxMpTemplateData("content", msg, WE_CHAT_MSG_COLOR));
        return dataList;
    }

    /**
     * 推送微信模板消息
     *
     * @param templateMsg 微信模板消息
     */
    protected void publishTemplateMsg(WxMpTemplateMessage templateMsg) {
//        WxMpTemplateMsgService wxMpTemplateMsgService = wxMpService.getTemplateMsgService();todo 等审核通过
//        try {
//            wxMpTemplateMsgService.sendTemplateMsg(templateMsg);
//        } catch (WxErrorException e) {
//            log.error("publish we chat msg failed! open id is {}, msg is {}.",
//                    templateMsg.getToUser(), JsonUtils.toStr(templateMsg.getData()));
//        }
    }
}
