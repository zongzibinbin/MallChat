package com.abin.mallchat.custom.common.event;

import com.abin.mallchat.common.user.domain.entity.User;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.util.Date;

@Getter
public class UserOnlineEvent extends ApplicationEvent {
    private User user;

    public UserOnlineEvent(Object source, User user) {
        super(source);
        this.user = user;
    }
}
