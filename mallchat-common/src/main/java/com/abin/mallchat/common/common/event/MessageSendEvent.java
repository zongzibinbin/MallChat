package com.abin.mallchat.common.common.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * @author Real
 * @date 2023/05/28 19:00
 */
@Getter
public class MessageSendEvent extends ApplicationEvent {
    private Long msgId;

    public MessageSendEvent(Object source, Long msgId) {
        super(source);
        this.msgId = msgId;
    }
}
