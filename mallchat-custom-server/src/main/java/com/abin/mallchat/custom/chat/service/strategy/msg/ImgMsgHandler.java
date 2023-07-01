package com.abin.mallchat.custom.chat.service.strategy.msg;

import cn.hutool.core.bean.BeanUtil;
import com.abin.mallchat.common.chat.dao.MessageDao;
import com.abin.mallchat.common.chat.domain.entity.Message;
import com.abin.mallchat.common.chat.domain.entity.msg.ImgMsgDTO;
import com.abin.mallchat.common.chat.domain.entity.msg.MessageExtra;
import com.abin.mallchat.common.chat.domain.enums.MessageTypeEnum;
import com.abin.mallchat.common.common.utils.AssertUtil;
import com.abin.mallchat.custom.chat.domain.vo.request.ChatMessageReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Description:图片消息
 * Author: <a href="https://github.com/zongzibinbin">abin</a>
 * Date: 2023-06-04
 */
@Component
public class ImgMsgHandler extends AbstractMsgHandler {
    @Autowired
    private MessageDao messageDao;

    @Override
    MessageTypeEnum getMsgTypeEnum() {
        return MessageTypeEnum.IMG;
    }

    @Override
    public void checkMsg(ChatMessageReq request, Long uid) {
        ImgMsgDTO body = BeanUtil.toBean(request.getBody(), ImgMsgDTO.class);
        AssertUtil.allCheckValidateThrow(body);
    }

    @Override
    public void saveMsg(Message msg, ChatMessageReq request) {
        ImgMsgDTO body = BeanUtil.toBean(request.getBody(), ImgMsgDTO.class);
        MessageExtra extra = Optional.ofNullable(msg.getExtra()).orElse(new MessageExtra());
        Message update = new Message();
        update.setId(msg.getId());
        update.setExtra(extra);
        extra.setImgMsgDTO(body);
        messageDao.updateById(update);
    }

    @Override
    public Object showMsg(Message msg) {
        return msg.getExtra().getImgMsgDTO();
    }

    @Override
    public Object showReplyMsg(Message msg) {
        return "图片";
    }
}
