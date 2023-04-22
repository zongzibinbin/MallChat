package com.abin.mallchat.custom.common.event.listener;

import com.abin.mallchat.common.common.domain.enums.IdempotentEnum;
import com.abin.mallchat.common.user.dao.UserDao;
import com.abin.mallchat.common.user.domain.entity.User;
import com.abin.mallchat.common.user.domain.enums.ItemEnum;
import com.abin.mallchat.common.user.service.IUserBackpackService;
import com.abin.mallchat.common.user.service.cache.UserCache;
import com.abin.mallchat.custom.common.event.UserOnlineEvent;
import com.abin.mallchat.custom.common.event.UserRegisterEvent;
import com.abin.mallchat.custom.user.service.WebSocketService;
import com.abin.mallchat.custom.user.service.adapter.WSAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * 用户上线监听器
 *
 * @author zhongzb create on 2022/08/26
 */
@Slf4j
@Component
public class UserRegisterListener {
    @Autowired
    private WebSocketService webSocketService;
    @Autowired
    private UserDao userDao;
    @Autowired
    private UserCache userCache;
    @Autowired
    private WSAdapter wsAdapter;
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
    public void sendBadge(UserOnlineEvent event) {
        User user = event.getUser();
        int count = userDao.count();
    }

}
