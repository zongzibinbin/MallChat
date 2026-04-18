package com.abin.mallchat.common.chat.service.strategy.msg;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import com.abin.mallchat.common.chat.dao.MessageDao;
import com.abin.mallchat.common.chat.domain.dto.ChatMsgRecallDTO;
import com.abin.mallchat.common.chat.domain.entity.Message;
import com.abin.mallchat.common.chat.domain.entity.msg.MessageExtra;
import com.abin.mallchat.common.chat.domain.entity.msg.MsgRecall;
import com.abin.mallchat.common.chat.domain.enums.MessageTypeEnum;
import com.abin.mallchat.common.chat.service.cache.MsgCache;
import com.abin.mallchat.common.common.event.MessageRecallEvent;
import com.abin.mallchat.common.common.utils.AssertUtil;
import com.abin.mallchat.common.user.domain.entity.User;
import com.abin.mallchat.common.user.domain.enums.RoleEnum;
import com.abin.mallchat.common.user.service.IRoleService;
import com.abin.mallchat.common.user.service.cache.UserCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Objects;

/**
 * Description: 撤回文本消息
 * Author: <a href="https://github.com/zongzibinbin">abin</a>
 * Date: 2023-06-04
 */
@Component
public class RecallMsgHandler extends AbstractMsgHandler<Object> {
    @Autowired
    private MessageDao messageDao;
    @Autowired
    private UserCache userCache;
    @Autowired
    private MsgCache msgCache;
    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;
    @Autowired
    private IRoleService iRoleService;

    @Override
    MessageTypeEnum getMsgTypeEnum() {
        return MessageTypeEnum.RECALL;
    }

    @Override
    public void saveMsg(Message msg, Object body) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object showMsg(Message msg) {
        MsgRecall recall = msg.getExtra().getRecall();
        User userInfo = userCache.getUserInfo(recall.getRecallUid());
        if (!Objects.equals(recall.getRecallUid(), msg.getFromUid())) {
            return "管理员\"" + userInfo.getName() + "\"撤回了一条成员消息";
        }
        return "\"" + userInfo.getName() + "\"撤回了一条消息";
    }

    @Override
    public Object showReplyMsg(Message msg) {
        return "原消息已被撤回";
    }

    public void recall(Long recallUid, Message message) {//todo 消息覆盖问题用版本号解决
        checkRecall(recallUid, message);
        MessageExtra extra = message.getExtra();
        extra.setRecall(new MsgRecall(recallUid, new Date()));
        Message update = new Message();
        update.setId(message.getId());
        update.setType(MessageTypeEnum.RECALL.getType());
        update.setExtra(extra);
        messageDao.updateById(update);
        applicationEventPublisher.publishEvent(new MessageRecallEvent(this, new ChatMsgRecallDTO(message.getId(), message.getRoomId(), recallUid)));

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

    @Override
    public String showContactMsg(Message msg) {
        return "撤回了一条消息";
    }
}
