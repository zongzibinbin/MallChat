package com.abin.mallchat.common.common.event;

import com.abin.mallchat.common.user.domain.entity.UserBackpack;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * @author Real
 * @date 2023/05/28 18:59
 */
@Getter
public class ItemReceiveEvent extends ApplicationEvent {

    private UserBackpack userBackpack;

    public ItemReceiveEvent(Object source, UserBackpack userBackpack) {
        super(source);
        this.userBackpack = userBackpack;
    }

}
