package com.abin.mallchat.common.chat.service.impl;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import com.abin.mallchat.common.chat.dao.MessageDao;
import com.abin.mallchat.common.chat.domain.entity.Message;
import com.abin.mallchat.common.chat.domain.enums.MessageTypeEnum;
import com.abin.mallchat.common.chat.domain.vo.request.ChatMessageBaseReq;
import com.abin.mallchat.common.chat.service.MessageRecallService;
import com.abin.mallchat.common.chat.service.strategy.msg.RecallMsgHandler;
import com.abin.mallchat.common.common.annotation.RedissonLock;
import com.abin.mallchat.common.common.utils.AssertUtil;
import com.abin.mallchat.common.user.domain.enums.RoleEnum;
import com.abin.mallchat.common.user.service.IRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Objects;

/**
 * Description: 消息撤回服务实现
 * Author: <a href="https://github.com/zongzibinbin">abin</a>
 * Date: 2023-06-04
 */
@Service
public class MessageRecallServiceImpl implements MessageRecallService {
    @Autowired
    private MessageDao messageDao;
    @Autowired
    private IRoleService iRoleService;
    @Autowired
    private RecallMsgHandler recallMsgHandler;

    @Override
    @RedissonLock(key = "#uid")
    public void recallMsg(Long uid, ChatMessageBaseReq request) {
        Message message = messageDao.getById(request.getMsgId());
        checkRecall(uid, message);
        recallMsgHandler.recall(uid, message);
    }

    private void checkRecall(Long uid, Message message) {
        AssertUtil.isNotEmpty(message, "消息有误");
        AssertUtil.notEqual(message.getType(), MessageTypeEnum.RECALL.getType(), "消息无法撤回");
        boolean hasPower = iRoleService.hasPower(uid, RoleEnum.CHAT_MANAGER);
        if (hasPower) {
            return;
        }
        boolean self = Objects.equals(uid, message.getFromUid());
        AssertUtil.isTrue(self, "抱歉,您没有权限");
        long between = DateUtil.between(message.getCreateTime(), new Date(), DateUnit.MINUTE);
        AssertUtil.isTrue(between < 2, "覆水难收，超过2分钟的消息不能撤回哦~~");
    }
}
