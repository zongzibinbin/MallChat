package com.abin.mallchat.common.common.event;

import com.abin.mallchat.common.user.domain.entity.UserBackpack;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * <p>
 * 用户收到物品事件
 * </p>
 *
 * @author <a href="https://github.com/zongzibinbin">abin</a>
 * @since 2023-03-28
 */
@Getter
public class ItemReceiveEvent extends ApplicationEvent {
    private UserBackpack userBackpack;

    public ItemReceiveEvent(Object source, UserBackpack userBackpack) {
        super(source);
        this.userBackpack = userBackpack;
    }

}
