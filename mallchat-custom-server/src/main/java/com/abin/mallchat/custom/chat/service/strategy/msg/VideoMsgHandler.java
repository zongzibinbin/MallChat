package com.abin.mallchat.custom.chat.service.strategy.msg;

import cn.hutool.core.bean.BeanUtil;
import com.abin.mallchat.common.chat.dao.MessageDao;
import com.abin.mallchat.common.chat.domain.entity.Message;
import com.abin.mallchat.common.chat.domain.entity.msg.MessageExtra;
import com.abin.mallchat.common.chat.domain.entity.msg.VideoMsgDTO;
import com.abin.mallchat.common.chat.domain.enums.MessageTypeEnum;
import com.abin.mallchat.common.common.utils.AssertUtil;
import com.abin.mallchat.custom.chat.domain.vo.request.ChatMessageReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Description:视频消息
 * Author: <a href="https://github.com/zongzibinbin">abin</a>
 * Date: 2023-06-04
 */
@Component
public class VideoMsgHandler extends AbstractMsgHandler {
    @Autowired
    private MessageDao messageDao;

    @Override
    MessageTypeEnum getMsgTypeEnum() {
        return MessageTypeEnum.VIDEO;
    }

    @Override
    public void checkMsg(ChatMessageReq request, Long uid) {
        VideoMsgDTO body = BeanUtil.toBean(request.getBody(), VideoMsgDTO.class);
        AssertUtil.allCheckValidateThrow(body);
    }

    @Override
    public void saveMsg(Message msg, ChatMessageReq request) {
        VideoMsgDTO body = BeanUtil.toBean(request.getBody(), VideoMsgDTO.class);
        MessageExtra extra = Optional.ofNullable(msg.getExtra()).orElse(new MessageExtra());
        Message update = new Message();
        update.setId(msg.getId());
        update.setExtra(extra);
        extra.setVideoMsgDTO(body);
        messageDao.updateById(update);
    }

    @Override
    public Object showMsg(Message msg) {
        return msg.getExtra().getVideoMsgDTO();
    }

    @Override
    public Object showReplyMsg(Message msg) {
        return "视频";
    }
}
