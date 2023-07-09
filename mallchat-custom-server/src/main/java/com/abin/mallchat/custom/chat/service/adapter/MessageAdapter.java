package com.abin.mallchat.custom.chat.service.adapter;

import cn.hutool.core.bean.BeanUtil;
import com.abin.mallchat.common.chat.domain.entity.Message;
import com.abin.mallchat.common.chat.domain.entity.MessageMark;
import com.abin.mallchat.common.chat.domain.enums.MessageMarkTypeEnum;
import com.abin.mallchat.common.chat.domain.enums.MessageStatusEnum;
import com.abin.mallchat.common.common.domain.enums.YesOrNoEnum;
import com.abin.mallchat.custom.chat.domain.vo.request.ChatMessageReq;
import com.abin.mallchat.custom.chat.domain.vo.response.ChatMessageResp;
import com.abin.mallchat.custom.chat.service.strategy.msg.AbstractMsgHandler;
import com.abin.mallchat.custom.chat.service.strategy.msg.MsgHandlerFactory;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Description: 消息适配器
 * Author: <a href="https://github.com/zongzibinbin">abin</a>
 * Date: 2023-03-26
 */
public class MessageAdapter {
    public static final int CAN_CALLBACK_GAP_COUNT = 100;

    public static Message buildMsgSave(ChatMessageReq request, Long uid) {

        return Message.builder()
                .fromUid(uid)
                .roomId(request.getRoomId())
                .type(request.getMsgType())
                .status(MessageStatusEnum.NORMAL.getStatus())
                .build();

    }

    public static List<ChatMessageResp> buildMsgResp(List<Message> messages, Map<Long, Message> replyMap, List<MessageMark> msgMark, Long receiveUid) {
        Map<Long, List<MessageMark>> markMap = msgMark.stream().collect(Collectors.groupingBy(MessageMark::getMsgId));
        return messages.stream().map(a -> {
            ChatMessageResp resp = new ChatMessageResp();
            resp.setFromUser(buildFromUser(a.getFromUid()));
            resp.setMessage(buildMessage(a, replyMap, markMap.getOrDefault(a.getId(), new ArrayList<>()), receiveUid));
            return resp;
        })
                .sorted(Comparator.comparing(a -> a.getMessage().getSendTime()))//帮前端排好序，更方便它展示
                .collect(Collectors.toList());
    }

    private static ChatMessageResp.Message buildMessage(Message message, Map<Long, Message> replyMap, List<MessageMark> marks, Long receiveUid) {
        ChatMessageResp.Message messageVO = new ChatMessageResp.Message();
        BeanUtil.copyProperties(message, messageVO);
        messageVO.setSendTime(message.getCreateTime());
        AbstractMsgHandler msgHandler = MsgHandlerFactory.getStrategyNoNull(message.getType());
        if (Objects.nonNull(msgHandler)) {
            messageVO.setBody(msgHandler.showMsg(message));
        }
        //消息标记
        messageVO.setMessageMark(buildMsgMark(marks, receiveUid));
        return messageVO;
    }

    private static ChatMessageResp.MessageMark buildMsgMark(List<MessageMark> marks, Long receiveUid) {
        Map<Integer, List<MessageMark>> typeMap = marks.stream().collect(Collectors.groupingBy(MessageMark::getType));
        List<MessageMark> likeMarks = typeMap.getOrDefault(MessageMarkTypeEnum.LIKE.getType(), new ArrayList<>());
        List<MessageMark> dislikeMarks = typeMap.getOrDefault(MessageMarkTypeEnum.DISLIKE.getType(), new ArrayList<>());
        ChatMessageResp.MessageMark mark = new ChatMessageResp.MessageMark();
        mark.setLikeCount(likeMarks.size());
        mark.setUserLike(Optional.ofNullable(receiveUid).filter(uid -> likeMarks.stream().anyMatch(a -> Objects.equals(a.getUid(), uid))).map(a -> YesOrNoEnum.YES.getStatus()).orElse(YesOrNoEnum.NO.getStatus()));
        mark.setDislikeCount(dislikeMarks.size());
        mark.setUserDislike(Optional.ofNullable(receiveUid).filter(uid -> dislikeMarks.stream().anyMatch(a -> Objects.equals(a.getUid(), uid))).map(a -> YesOrNoEnum.YES.getStatus()).orElse(YesOrNoEnum.NO.getStatus()));
        return mark;
    }

    private static ChatMessageResp.UserInfo buildFromUser(Long fromUid) {
        ChatMessageResp.UserInfo userInfo = new ChatMessageResp.UserInfo();
        userInfo.setUid(fromUid);
        return userInfo;
    }


}
