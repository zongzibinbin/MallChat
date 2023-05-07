package com.abin.mallchat.custom.chat.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Pair;
import com.abin.mallchat.common.chat.dao.MessageDao;
import com.abin.mallchat.common.chat.dao.MessageMarkDao;
import com.abin.mallchat.common.chat.dao.RoomDao;
import com.abin.mallchat.common.chat.domain.dto.ChatMessageMarkDTO;
import com.abin.mallchat.common.chat.domain.entity.Message;
import com.abin.mallchat.common.chat.domain.entity.MessageMark;
import com.abin.mallchat.common.chat.domain.entity.Room;
import com.abin.mallchat.common.common.domain.enums.YesOrNoEnum;
import com.abin.mallchat.common.common.domain.vo.request.CursorPageBaseReq;
import com.abin.mallchat.common.common.domain.vo.response.CursorPageBaseResp;
import com.abin.mallchat.common.common.exception.BusinessException;
import com.abin.mallchat.common.common.utils.AssertUtil;
import com.abin.mallchat.common.user.dao.UserDao;
import com.abin.mallchat.common.user.domain.entity.User;
import com.abin.mallchat.common.user.domain.enums.ChatActiveStatusEnum;
import com.abin.mallchat.common.user.service.cache.UserCache;
import com.abin.mallchat.custom.chat.domain.vo.request.ChatMessageMarkReq;
import com.abin.mallchat.custom.chat.domain.vo.request.ChatMessagePageReq;
import com.abin.mallchat.custom.chat.domain.vo.request.ChatMessageReq;
import com.abin.mallchat.custom.chat.domain.vo.response.ChatMemberResp;
import com.abin.mallchat.custom.chat.domain.vo.response.ChatMemberStatisticResp;
import com.abin.mallchat.custom.chat.domain.vo.response.ChatMessageResp;
import com.abin.mallchat.custom.chat.domain.vo.response.ChatRoomResp;
import com.abin.mallchat.custom.chat.service.adapter.MemberAdapter;
import com.abin.mallchat.custom.chat.service.adapter.RoomAdapter;
import com.abin.mallchat.custom.chat.service.helper.ChatMemberHelper;
import com.abin.mallchat.common.common.event.MessageMarkEvent;
import com.abin.mallchat.common.common.event.MessageSendEvent;
import com.abin.mallchat.custom.chat.service.ChatService;
import com.abin.mallchat.custom.chat.service.adapter.MessageAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Description: 消息处理类
 * Author: <a href="https://github.com/zongzibinbin">abin</a>
 * Date: 2023-03-26
 */
@Service
@Slf4j
public class ChatServiceImpl implements ChatService {
    public static final long ROOM_GROUP_ID = 1L;
    @Autowired
    private MessageDao messageDao;
    @Autowired
    private UserDao userDao;
    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;
    @Autowired
    private UserCache userCache;
    @Autowired
    private MemberAdapter memberAdapter;
    @Autowired
    private RoomDao roomDao;
    @Autowired
    private MessageMarkDao messageMarkDao;

    /**
     * 发送消息
     *
     * @param request
     */
    @Override
    @Transactional
    public Long sendMsg(ChatMessageReq request, Long uid) {
        //校验下回复消息
        Message replyMsg = null;
        if (Objects.nonNull(request.getReplyMsgId())) {
            replyMsg = messageDao.getById(request.getReplyMsgId());
            AssertUtil.isNotEmpty(replyMsg, "回复消息不存在");
            AssertUtil.equal(replyMsg.getRoomId(), request.getRoomId(), "只能回复相同会话内的消息");

        }
        Message insert = MessageAdapter.buildMsgSave(request, uid);
        messageDao.save(insert);
        //如果有回复消息
        if (Objects.nonNull(replyMsg)) {
            Integer gapCount = messageDao.getGapCount(request.getRoomId(), replyMsg.getId(), insert.getId());
            messageDao.updateGapCount(insert.getId(), gapCount);
        }
        //发布消息发送事件
        applicationEventPublisher.publishEvent(new MessageSendEvent(this, insert.getId()));
        return insert.getId();
    }

    @Override
    public ChatMessageResp getMsgResp(Message message, Long receiveUid) {
        return CollUtil.getFirst(getMsgRespBatch(Collections.singletonList(message), receiveUid));
    }

    @Override
    public ChatMessageResp getMsgResp(Long msgId, Long receiveUid) {
        Message msg = messageDao.getById(msgId);
        return getMsgResp(msg, receiveUid);
    }

    @Override
    public CursorPageBaseResp<ChatMemberResp> getMemberPage(CursorPageBaseReq request) {
        Pair<ChatActiveStatusEnum, String> pair = ChatMemberHelper.getCursorPair(request.getCursor());
        ChatActiveStatusEnum activeStatusEnum = pair.getKey();
        String timeCursor = pair.getValue();
        List<ChatMemberResp> resultList = new ArrayList<>();//最终列表
        Boolean isLast = Boolean.FALSE;
        if (activeStatusEnum == ChatActiveStatusEnum.ONLINE) {//在线列表
            CursorPageBaseResp<Pair<Long, Double>> cursorPage = userCache.getOnlineCursorPage(new CursorPageBaseReq(request.getPageSize(), timeCursor));
            resultList.addAll(memberAdapter.buildMember(cursorPage.getList(), ChatActiveStatusEnum.ONLINE));//添加在线列表
            if (cursorPage.getIsLast()) {//如果是最后一页,从离线列表再补点数据
                Integer leftSize = request.getPageSize() - cursorPage.getList().size();
                cursorPage = userCache.getOfflineCursorPage(new CursorPageBaseReq(leftSize, null));
                resultList.addAll(memberAdapter.buildMember(cursorPage.getList(), ChatActiveStatusEnum.OFFLINE));//添加离线线列表
                activeStatusEnum = ChatActiveStatusEnum.OFFLINE;
            }
            timeCursor = cursorPage.getCursor();
            isLast = cursorPage.getIsLast();
        } else if (activeStatusEnum == ChatActiveStatusEnum.OFFLINE) {//离线列表
            CursorPageBaseResp<Pair<Long, Double>> cursorPage = userCache.getOfflineCursorPage(new CursorPageBaseReq(request.getPageSize(), timeCursor));
            resultList.addAll(memberAdapter.buildMember(cursorPage.getList(), ChatActiveStatusEnum.OFFLINE));//添加离线线列表
            timeCursor = cursorPage.getCursor();
            isLast = cursorPage.getIsLast();
        }
        //组装结果
        return new CursorPageBaseResp<>(ChatMemberHelper.generateCursor(activeStatusEnum, timeCursor), isLast, resultList);
    }

    @Override
    public CursorPageBaseResp<ChatMessageResp> getMsgPage(ChatMessagePageReq request, Long receiveUid) {
        CursorPageBaseResp<Message> cursorPage = messageDao.getCursorPage(request.getRoomId(), request);
        if (cursorPage.isEmpty()) {
            return CursorPageBaseResp.empty();
        }
        return CursorPageBaseResp.init(cursorPage, getMsgRespBatch(cursorPage.getList(), receiveUid));
    }

    @Override
    public CursorPageBaseResp<ChatRoomResp> getRoomPage(CursorPageBaseReq request, Long uid) {
        CursorPageBaseResp<Room> cursorPage = roomDao.getCursorPage(request);
        if (request.isFirstPage()) {
            //第一页插入置顶的大群聊
            Room group = roomDao.getById(ROOM_GROUP_ID);
            cursorPage.getList().add(0, group);
        }
        return CursorPageBaseResp.init(cursorPage, RoomAdapter.buildResp(cursorPage.getList()));
    }

    @Override
    public ChatMemberStatisticResp getMemberStatistic() {
        System.out.println(Thread.currentThread().getName());
        Long onlineNum = userCache.getOnlineNum();
        Long offlineNum = userCache.getOfflineNum();
        ChatMemberStatisticResp resp = new ChatMemberStatisticResp();
        resp.setOnlineNum(onlineNum);
        resp.setTotalNum(onlineNum + offlineNum);
        return resp;
    }

    @Override
    public void setMsgMark(Long uid, ChatMessageMarkReq request) {
        //用户对该消息的标记
        MessageMark messageMark = messageMarkDao.get(uid, request.getMsgId(), request.getMarkType());
        if (Objects.nonNull(messageMark)) {//有标记过消息修改一下就好
            MessageMark update = MessageMark.builder()
                    .id(messageMark.getId())
                    .status(transformAct(request.getActType()))
                    .build();
            messageMarkDao.updateById(update);
        }
        //没标记过消息，插入一条新消息
        MessageMark insert = MessageMark.builder()
                .uid(uid)
                .msgId(request.getMsgId())
                .type(request.getMarkType())
                .status(transformAct(request.getActType()))
                .build();
        messageMarkDao.save(insert);
        //发布消息标记事件
        ChatMessageMarkDTO dto = new ChatMessageMarkDTO();
        BeanUtils.copyProperties(request, dto);
        applicationEventPublisher.publishEvent(new MessageMarkEvent(this, dto));
    }

    private Integer transformAct(Integer actType) {
        if (actType == 1) {
            return YesOrNoEnum.NO.getStatus();
        } else if (actType == 2) {
            return YesOrNoEnum.YES.getStatus();
        }
        throw new BusinessException("动作类型 1确认 2取消");
    }

    public List<ChatMessageResp> getMsgRespBatch(List<Message> messages, Long receiveUid) {
        if (CollectionUtil.isEmpty(messages)) {
            return new ArrayList<>();
        }
        Map<Long, Message> replyMap = new HashMap<>();
        Map<Long, User> userMap = new HashMap<>();
        //批量查出回复的消息
        List<Long> replyIds = messages.stream().map(Message::getReplyMsgId).filter(Objects::nonNull).distinct().collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(replyIds)) {
            replyMap = messageDao.listByIds(replyIds).stream().collect(Collectors.toMap(Message::getId, Function.identity()));
        }
        //批量查询消息关联用户
        Set<Long> uidSet = Stream.concat(replyMap.values().stream().map(Message::getFromUid), messages.stream().map(Message::getFromUid)).collect(Collectors.toSet());
        userMap = userCache.getUserInfoBatch(uidSet);
        //查询消息标志
        List<MessageMark> msgMark = messageMarkDao.getValidMarkByMsgIdBatch(messages.stream().map(Message::getId).collect(Collectors.toList()));
        return MessageAdapter.buildMsgResp(messages, replyMap, userMap, msgMark, receiveUid);
    }

}
