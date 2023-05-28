package com.abin.mallchat.common.common.event;

import com.abin.mallchat.common.user.domain.entity.User;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * @author Real
 * @date 2023/05/28 19:00
 */
@Getter
public class UserOfflineEvent extends ApplicationEvent {
    private User user;

    public UserOfflineEvent(Object source, User user) {
        super(source);
        this.user = user;
    }
}
