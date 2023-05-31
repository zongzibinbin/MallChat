package com.abin.mallchat.custom.common.event.listener;

import com.abin.mallchat.common.common.domain.enums.IdempotentEnum;
import com.abin.mallchat.common.common.event.UserRegisterEvent;
import com.abin.mallchat.common.user.dao.UserDao;
import com.abin.mallchat.common.user.domain.entity.User;
import com.abin.mallchat.common.user.domain.enums.ItemEnum;
import com.abin.mallchat.common.user.service.IUserBackpackService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * <p>
 * 用户注册监听器
 * </p>
 *
 * @author <a href="https://github.com/zongzibinbin">abin</a>
 * @since 2022-08-26
 */
@Slf4j
@Component
public class UserRegisterListener {

    @Autowired
    private UserDao userDao;
    @Autowired
    private IUserBackpackService iUserBackpackService;

    @Async
    @EventListener(classes = UserRegisterEvent.class)
    public void sendCard(UserRegisterEvent event) {
        User user = event.getUser();
        //送一张改名卡
        iUserBackpackService.acquireItem(user.getId(), ItemEnum.MODIFY_NAME_CARD.getId(), IdempotentEnum.UID, user.getId().toString());
    }

    @Async
    @EventListener(classes = UserRegisterEvent.class)
    public void sendBadge(UserRegisterEvent event) {
        User user = event.getUser();
        //todo 性能瓶颈，等注册用户多了直接删掉
        int count = userDao.count();
        if (count <= 10) {
            iUserBackpackService.acquireItem(user.getId(), ItemEnum.REG_TOP10_BADGE.getId(), IdempotentEnum.UID, user.getId().toString());
        } else if (count <= 100) {
            iUserBackpackService.acquireItem(user.getId(), ItemEnum.REG_TOP100_BADGE.getId(), IdempotentEnum.UID, user.getId().toString());
        }
    }

}
