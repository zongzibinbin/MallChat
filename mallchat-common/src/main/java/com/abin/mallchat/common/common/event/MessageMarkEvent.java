package com.abin.mallchat.common.common.event;

import com.abin.mallchat.common.chat.domain.dto.ChatMessageMarkDTO;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * @author Real
 * @date 2023/05/28 18:59
 */
@Getter
public class MessageMarkEvent extends ApplicationEvent {
    private ChatMessageMarkDTO dto;

    public MessageMarkEvent(Object source, ChatMessageMarkDTO dto) {
        super(source);
        this.dto = dto;
    }
}
