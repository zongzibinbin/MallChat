package com.abin.mallchat.custom.chat.service.strategy.mark;

import com.abin.mallchat.common.chat.domain.enums.MessageMarkTypeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

/**
 * Description: 点踩标记策略类
 * Author: <a href="https://github.com/zongzibinbin">abin</a>
 * Date: 2023-05-30
 */
@Component
public class DisLikeStrategy extends AbstractMsgMarkStrategy {
    @Autowired
    @Lazy
    private LikeStrategy likeStrategy;

    @Override
    protected MessageMarkTypeEnum getTypeEnum() {
        return MessageMarkTypeEnum.DISLIKE;
    }

    @Override
    public void mark(Long uid, Long msgId) {
        super.mark(uid, msgId);
        //同时取消点赞的动作
        MsgMarkFactory.getStrategyNoNull(MessageMarkTypeEnum.LIKE.getType()).unMark(uid, msgId);
    }

}
