package com.abin.mallchat.custom.openai.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class OpenAIEvent extends ApplicationEvent {
    private Long msgId;

    public OpenAIEvent(Object source, Long msgId) {
        super(source);
        this.msgId = msgId;
    }
}