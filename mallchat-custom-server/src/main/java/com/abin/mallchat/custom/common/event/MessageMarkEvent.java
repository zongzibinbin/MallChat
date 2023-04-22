package com.abin.mallchat.custom.common.event;

import com.abin.mallchat.custom.chat.domain.vo.request.ChatMessageMarkReq;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class MessageMarkEvent extends ApplicationEvent {
    private ChatMessageMarkReq req;

    public MessageMarkEvent(Object source, ChatMessageMarkReq req) {
        super(source);
        this.req = req;
    }
}
