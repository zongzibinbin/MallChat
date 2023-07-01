package com.abin.mallchat.custom.openai.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.thread.NamedThreadFactory;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.abin.mallchat.common.chat.domain.entity.Message;
import com.abin.mallchat.common.chat.domain.enums.MessageTypeEnum;
import com.abin.mallchat.common.common.constant.RedisKey;
import com.abin.mallchat.common.common.exception.BusinessException;
import com.abin.mallchat.common.common.handler.GlobalUncaughtExceptionHandler;
import com.abin.mallchat.common.common.utils.DateUtils;
import com.abin.mallchat.common.common.utils.RedisUtils;
import com.abin.mallchat.custom.chat.domain.vo.request.ChatMessageReq;
import com.abin.mallchat.custom.chat.domain.vo.request.msg.TextMsgReq;
import com.abin.mallchat.custom.chat.service.ChatService;
import com.abin.mallchat.custom.openai.enums.OpenAIModelEnums;
import com.abin.mallchat.custom.openai.service.IOpenAIService;
import com.abin.mallchat.custom.openai.utils.OpenAIUtils;
import com.abin.mallchat.custom.user.domain.vo.response.user.UserInfoResp;
import com.abin.mallchat.custom.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Description;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class OpenAIServiceImpl implements IOpenAIService, DisposableBean, InitializingBean {
    private static ExecutorService EXECUTOR;

    @Value("${openai.use-openai:false}")
    private boolean USE_OPENAI;
    @Value("${openai.ai-user-id}")
    public Long AI_USER_ID;

    @Value("${openai.model.name:text-davinci-003}")
    private String modelName;
    @Value("${openai.key}")
    private String key;
    @Value("${openai.proxy-url:}")
    private String proxyUrl;

    @Value("${openai.limit:5}")
    private Integer limit;

    @Autowired
    private UserService userService;
    @Lazy
    @Autowired
    private ChatService chatService;

    public static String MALL_CHAT_AI_NAME;

    /**
     * 聊天
     *
     * @param chatMessageReq 提示词
     * @param uid           用户id
     */
    @Deprecated
    @Override
    public void chat(ChatMessageReq chatMessageReq, Long uid) {
        TextMsgReq body = BeanUtil.toBean(chatMessageReq.getBody(), TextMsgReq.class);
        String content = body.getContent().replace(MALL_CHAT_AI_NAME, "").trim();
        EXECUTOR.execute(() -> {
            Long chatNum;
            if ((chatNum = userChatNumInrc(uid)) > limit) {
                answerMsg("你今天已经和我聊了" + chatNum + "次了，我累了，明天再聊吧", chatMessageReq.getRoomId(), uid);
            } else {
                chat(content, chatMessageReq.getRoomId(), uid);
            }
        });

    }

    @Override
    public void chat(Message message) {
        String content = message.getContent().replace(MALL_CHAT_AI_NAME, "").trim();
        Long roomId = message.getRoomId();
        Long uid = message.getFromUid();
        EXECUTOR.execute(() -> {
            Long chatNum;
            if ((chatNum = userChatNumInrc(uid)) > limit) {
                answerMsg("你今天已经和我聊了" + chatNum + "次了，我累了，明天再聊吧", roomId, uid);
            } else {
                chat(content, roomId, uid);
            }
        });

    }

    private Long userChatNumInrc(Long uid) {
        //todo:白名单
        return RedisUtils.inc(RedisKey.getKey(RedisKey.USER_CHAT_NUM, uid), DateUtils.getEndTimeByToday().intValue(), TimeUnit.MILLISECONDS);
    }

    private void chat(String content, Long roomId, Long uid) {
        HttpResponse response = OpenAIUtils.create(key)
                .proxyUrl(proxyUrl)
                .model(modelName)
                .prompt(content)
                .send();
        String text = OpenAIUtils.parseText(response);
        answerMsg(text, roomId, uid);
    }

    private void answerMsg(String text, Long roomId, Long uid) {
        ChatMessageReq answerReq = new ChatMessageReq();
        answerReq.setRoomId(roomId);
        answerReq.setMsgType(MessageTypeEnum.TEXT.getType());
        UserInfoResp userInfo = userService.getUserInfo(uid);
        TextMsgReq textMsgReq = new TextMsgReq();
        textMsgReq.setContent("@" + userInfo.getName() + " " + text);
        textMsgReq.setAtUidList(Collections.singletonList(uid));
        answerReq.setBody(textMsgReq);
        chatService.sendMsg(answerReq, AI_USER_ID);
    }


    @Override
    public void afterPropertiesSet() {
        if (!USE_OPENAI) {
            return;
        }
        if (StringUtils.isNotBlank(proxyUrl) && !HttpUtil.isHttp(proxyUrl) && !HttpUtil.isHttps(proxyUrl)) {
            throw new BusinessException("openai.proxy-url 配置错误");
        }
        OpenAIModelEnums modelEnum = OpenAIModelEnums.of(modelName);
        if (modelEnum == null) {
            throw new BusinessException("openai.model.name 配置错误");
        }
        Integer rpm = modelEnum.getRPM();
        EXECUTOR = new ThreadPoolExecutor(10, 10,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(rpm),
                new NamedThreadFactory("openAI-chat-gpt",
                        null,
                        false,
                        new GlobalUncaughtExceptionHandler()),
                (r, executor) -> {
                    throw new BusinessException("别问的太快了，我的脑子不够用了");
                });
        UserInfoResp userInfo = userService.getUserInfo(AI_USER_ID);
        if (userInfo == null) {
            throw new BusinessException("openai.ai-user-id 配置错误");
        }
        MALL_CHAT_AI_NAME = userInfo.getName();
    }

    @Override
    public void destroy() throws Exception {
        EXECUTOR.shutdown();
        if (!EXECUTOR.awaitTermination(30, TimeUnit.SECONDS)) { //最多等30秒，处理不完就拉倒
            if (log.isErrorEnabled()) {
                log.error("Timed out while waiting for executor [{}] to terminate", EXECUTOR);
            }
        }
    }
}