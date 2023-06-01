package com.abin.mallchat.custom.common.event.listener;

import com.abin.mallchat.common.common.event.UserOnlineEvent;
import com.abin.mallchat.common.user.dao.UserDao;
import com.abin.mallchat.common.user.domain.entity.User;
import com.abin.mallchat.common.user.service.IpService;
import com.abin.mallchat.common.user.service.cache.UserCache;
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
public class UserOnlineListener {
    @Autowired
    private WebSocketService webSocketService;
    @Autowired
    private UserDao userDao;
    @Autowired
    private UserCache userCache;
    @Autowired
    private WSAdapter wsAdapter;
    @Autowired
    private IpService ipService;

    @Async
    @EventListener(classes = UserOnlineEvent.class)
    public void saveRedisAndPush(UserOnlineEvent event) {
        User user = event.getUser();
        userCache.online(user.getId(), user.getLastOptTime());
        //推送给所有在线用户，该用户登录成功
        webSocketService.sendToAllOnline(wsAdapter.buildOnlineNotifyResp(event.getUser()));
    }

    @Async
    @EventListener(classes = UserOnlineEvent.class)
    public void saveDB(UserOnlineEvent event) {
        User user = event.getUser();
        User update = new User();
        update.setId(user.getId());
        update.setLastOptTime(user.getLastOptTime());
        update.setIpInfo(user.getIpInfo());
        userDao.updateById(update);
        //更新用户ip详情
        ipService.refreshIpDetailAsync(user.getId());
    }

}
