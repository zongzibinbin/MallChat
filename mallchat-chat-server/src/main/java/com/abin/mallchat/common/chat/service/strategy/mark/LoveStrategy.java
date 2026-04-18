package com.abin.mallchat.common.chat.service.strategy.mark;

import com.abin.mallchat.common.chat.domain.enums.MessageMarkTypeEnum;
import org.springframework.stereotype.Component;

@Component
public class LoveStrategy extends AbstractMsgMarkStrategy {

    @Override
    protected MessageMarkTypeEnum getTypeEnum() {
        return MessageMarkTypeEnum.LOVE;
    }
}
