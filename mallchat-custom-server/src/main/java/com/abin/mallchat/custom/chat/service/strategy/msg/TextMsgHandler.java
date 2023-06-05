package com.abin.mallchat.custom.chat.service.strategy.msg;

import cn.hutool.core.bean.BeanUtil;
import com.abin.mallchat.common.chat.dao.MessageDao;
import com.abin.mallchat.common.chat.domain.entity.Message;
import com.abin.mallchat.common.chat.domain.entity.msg.MessageExtra;
import com.abin.mallchat.common.chat.domain.enums.MessageStatusEnum;
import com.abin.mallchat.common.chat.domain.enums.MessageTypeEnum;
import com.abin.mallchat.common.chat.service.cache.MsgCache;
import com.abin.mallchat.common.common.domain.enums.YesOrNoEnum;
import com.abin.mallchat.common.common.utils.AssertUtil;
import com.abin.mallchat.common.user.domain.entity.User;
import com.abin.mallchat.common.user.service.cache.UserCache;
import com.abin.mallchat.custom.chat.domain.vo.request.ChatMessageReq;
import com.abin.mallchat.custom.chat.domain.vo.request.msg.TextMsgReq;
import com.abin.mallchat.custom.chat.domain.vo.response.msg.TextMsgResp;
import com.abin.mallchat.custom.chat.service.adapter.MessageAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;

/**
 * Description: 普通文本消息
 * Author: <a href="https://github.com/zongzibinbin">abin</a>
 * Date: 2023-06-04
 */
@Component
public class TextMsgHandler extends AbstractMsgHandler {
    @Autowired
    private MessageDao messageDao;
    @Autowired
    private MsgCache msgCache;
    @Autowired
    private UserCache userCache;

    @Override
    MessageTypeEnum getMsgTypeEnum() {
        return MessageTypeEnum.TEXT;
    }

    @Override
    public void checkMsg(ChatMessageReq request) {
        TextMsgReq body = BeanUtil.toBean(request.getBody(), TextMsgReq.class);
        AssertUtil.isNotEmpty(body.getContent(), "内容不能为空");
        AssertUtil.isTrue(body.getContent().length() < 500, "消息内容过长，服务器扛不住啊，兄dei");
        //校验下回复消息
        if (Objects.nonNull(body.getReplyMsgId())) {
            Message replyMsg = messageDao.getById(body.getReplyMsgId());
            AssertUtil.isNotEmpty(replyMsg, "回复消息不存在");
            AssertUtil.equal(replyMsg.getRoomId(), request.getRoomId(), "只能回复相同会话内的消息");
        }
    }

    @Override
    public void saveMsg(Message msg, ChatMessageReq request) {//插入文本内容
        TextMsgReq body = BeanUtil.toBean(request.getBody(), TextMsgReq.class);
        Message update = new Message();
        update.setId(msg.getId());
        update.setContent(body.getContent());
        //如果有回复消息
        if (Objects.nonNull(body.getReplyMsgId())) {
            Integer gapCount = messageDao.getGapCount(request.getRoomId(), body.getReplyMsgId(), msg.getId());
            update.setGapCount(gapCount);
            update.setReplyMsgId(body.getReplyMsgId());

        }
        messageDao.updateById(update);
    }

    @Override
    public Object showMsg(Message msg) {
        TextMsgResp resp = new TextMsgResp();
        resp.setContent(msg.getContent());
        resp.setUrlTitleMap(Optional.ofNullable(msg.getExtra()).map(MessageExtra::getUrlTitleMap).orElse(null));
        //回复消息
        Optional<Message> reply = Optional.ofNullable(msg.getReplyMsgId())
                .map(msgCache::getMsg)
                .filter(a -> Objects.equals(a.getStatus(), MessageStatusEnum.NORMAL.getStatus()));
        if (reply.isPresent()) {
            Message replyMessage = reply.get();
            TextMsgResp.ReplyMsg replyMsgVO = new TextMsgResp.ReplyMsg();
            replyMsgVO.setId(replyMessage.getId());
            replyMsgVO.setUid(replyMessage.getFromUid());
            replyMessage.setType(replyMessage.getType());
            replyMsgVO.setBody(MsgHandlerFactory.getStrategyNoNull(replyMessage.getType()).showReplyMsg(replyMessage));
            User replyUser = userCache.getUserInfo(replyMessage.getFromUid());
            replyMsgVO.setUsername(replyUser.getName());
            replyMsgVO.setCanCallback(YesOrNoEnum.toStatus(Objects.nonNull(msg.getGapCount()) && msg.getGapCount() <= MessageAdapter.CAN_CALLBACK_GAP_COUNT));
            replyMsgVO.setGapCount(msg.getGapCount());
            resp.setReply(replyMsgVO);
        }
        return resp;
    }

    @Override
    public Object showReplyMsg(Message msg) {
        return msg.getContent();
    }
}
