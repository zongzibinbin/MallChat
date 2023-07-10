package com.abin.mallchat.custom.chat.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Pair;
import com.abin.mallchat.common.chat.dao.MessageDao;
import com.abin.mallchat.common.chat.dao.MessageMarkDao;
import com.abin.mallchat.common.chat.dao.RoomDao;
import com.abin.mallchat.common.chat.domain.entity.Message;
import com.abin.mallchat.common.chat.domain.entity.MessageMark;
import com.abin.mallchat.common.chat.domain.entity.Room;
import com.abin.mallchat.common.chat.domain.enums.MessageMarkActTypeEnum;
import com.abin.mallchat.common.chat.domain.enums.MessageTypeEnum;
import com.abin.mallchat.common.common.annotation.RedissonLock;
import com.abin.mallchat.common.common.domain.vo.request.CursorPageBaseReq;
import com.abin.mallchat.common.common.domain.vo.response.CursorPageBaseResp;
import com.abin.mallchat.common.common.event.MessageSendEvent;
import com.abin.mallchat.common.common.utils.AssertUtil;
import com.abin.mallchat.common.user.dao.UserDao;
import com.abin.mallchat.common.user.domain.enums.ChatActiveStatusEnum;
import com.abin.mallchat.common.user.domain.enums.RoleEnum;
import com.abin.mallchat.common.user.service.IRoleService;
import com.abin.mallchat.common.user.service.cache.ItemCache;
import com.abin.mallchat.common.user.service.cache.UserCache;
import com.abin.mallchat.custom.chat.domain.vo.request.*;
import com.abin.mallchat.custom.chat.domain.vo.response.*;
import com.abin.mallchat.custom.chat.service.ChatService;
import com.abin.mallchat.custom.chat.service.adapter.MemberAdapter;
import com.abin.mallchat.custom.chat.service.adapter.MessageAdapter;
import com.abin.mallchat.custom.chat.service.adapter.RoomAdapter;
import com.abin.mallchat.custom.chat.service.helper.ChatMemberHelper;
import com.abin.mallchat.custom.chat.service.strategy.mark.AbstractMsgMarkStrategy;
import com.abin.mallchat.custom.chat.service.strategy.mark.MsgMarkFactory;
import com.abin.mallchat.custom.chat.service.strategy.msg.AbstractMsgHandler;
import com.abin.mallchat.custom.chat.service.strategy.msg.MsgHandlerFactory;
import com.abin.mallchat.custom.chat.service.strategy.msg.RecallMsgHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

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
    @Autowired
    private ItemCache itemCache;
    @Autowired
    private IRoleService iRoleService;
    @Autowired
    private RecallMsgHandler recallMsgHandler;

    /**
     * 发送消息
     */
    @Override
    @Transactional
    public Long sendMsg(ChatMessageReq request, Long uid) {
        AbstractMsgHandler msgHandler = MsgHandlerFactory.getStrategyNoNull(request.getMsgType());//todo 这里先不扩展，后续再改
        msgHandler.checkMsg(request, uid);
        //同步获取消息的跳转链接标题
        Message insert = MessageAdapter.buildMsgSave(request, uid);
        messageDao.save(insert);
        msgHandler.saveMsg(insert, request);
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
        ArrayList<Room> rooms = new ArrayList<>(cursorPage.getList());
        if (request.isFirstPage()) {
            //第一页插入置顶的大群聊
            Room group = roomDao.getById(ROOM_GROUP_ID);
            rooms.add(0, group);
        }
        return CursorPageBaseResp.init(cursorPage, RoomAdapter.buildResp(rooms));
    }

    @Override
    public ChatMemberStatisticResp getMemberStatistic() {
        System.out.println(Thread.currentThread().getName());
        Long onlineNum = userCache.getOnlineNum();
//        Long offlineNum = userCache.getOfflineNum();不展示总人数
        ChatMemberStatisticResp resp = new ChatMemberStatisticResp();
        resp.setOnlineNum(onlineNum);
//        resp.setTotalNum(onlineNum + offlineNum);
        return resp;
    }

    @Override
    @RedissonLock(key = "#uid")
    public void setMsgMark(Long uid, ChatMessageMarkReq request) {
        AbstractMsgMarkStrategy strategy = MsgMarkFactory.getStrategyNoNull(request.getMarkType());
        switch (MessageMarkActTypeEnum.of(request.getActType())) {
            case MARK:
                strategy.mark(uid, request.getMsgId());
                break;
            case UN_MARK:
                strategy.unMark(uid, request.getMsgId());
                break;
        }
    }

    @Override
    public void recallMsg(Long uid, ChatMessageBaseReq request) {
        Message message = messageDao.getById(request.getMsgId());
        //校验能不能执行撤回
        checkRecall(uid, message);
        //执行消息撤回
        recallMsgHandler.recall(uid, message);
    }

    @Override
    @Cacheable(cacheNames = "member", key = "'memberList.'+#req.roomId")
    public List<ChatMemberListResp> getMemberList(ChatMessageMemberReq req) {
        if (Objects.equals(1L, req.getRoomId())) {//大群聊可看见所有人
            return userDao.getMemberList()
                    .stream()
                    .map(a -> {
                        ChatMemberListResp resp = new ChatMemberListResp();
                        BeanUtils.copyProperties(a, resp);
                        resp.setUid(a.getId());
                        return resp;
                    }).collect(Collectors.toList());
        }
        return null;
    }

    private void checkRecall(Long uid, Message message) {
        AssertUtil.isNotEmpty(message, "消息有误");
        AssertUtil.notEqual(message.getType(), MessageTypeEnum.RECALL, "消息无法撤回");
        boolean hasPower = iRoleService.hasPower(uid, RoleEnum.CHAT_MANAGER);
        if (hasPower) {
            return;
        }
        boolean self = Objects.equals(uid, message.getFromUid());
        AssertUtil.isTrue(self, "抱歉,您没有权限");
        long between = DateUtil.between(message.getCreateTime(), new Date(), DateUnit.MINUTE);
        AssertUtil.isTrue(between < 2, "覆水难收，超过2分钟的消息不能撤回哦~~");
    }

    public List<ChatMessageResp> getMsgRespBatch(List<Message> messages, Long receiveUid) {
        if (CollectionUtil.isEmpty(messages)) {
            return new ArrayList<>();
        }
        Map<Long, Message> replyMap = new HashMap<>();
        //批量查出回复的消息
        List<Long> replyIds = messages.stream().map(Message::getReplyMsgId).filter(Objects::nonNull).distinct().collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(replyIds)) {
            replyMap = messageDao.listByIds(replyIds).stream().collect(Collectors.toMap(Message::getId, Function.identity()));
        }
        //查询消息标志
        List<MessageMark> msgMark = messageMarkDao.getValidMarkByMsgIdBatch(messages.stream().map(Message::getId).collect(Collectors.toList()));
        return MessageAdapter.buildMsgResp(messages, replyMap, msgMark, receiveUid);
    }

}
