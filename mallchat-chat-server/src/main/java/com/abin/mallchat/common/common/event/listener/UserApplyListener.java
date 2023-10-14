package com.abin.mallchat.common.common.event.listener;

import com.abin.mallchat.common.common.event.UserApplyEvent;
import com.abin.mallchat.common.user.dao.UserApplyDao;
import com.abin.mallchat.common.user.domain.entity.UserApply;
import com.abin.mallchat.common.user.domain.vo.response.ws.WSFriendApply;
import com.abin.mallchat.common.user.service.WebSocketService;
import com.abin.mallchat.common.user.service.adapter.WSAdapter;
import com.abin.mallchat.common.user.service.impl.PushService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * 好友申请监听器
 *
 * @author zhongzb create on 2022/08/26
 */
@Slf4j
@Component
public class UserApplyListener {
    @Autowired
    private UserApplyDao userApplyDao;
    @Autowired
    private WebSocketService webSocketService;

    @Autowired
    private PushService pushService;

    @Async
    @TransactionalEventListener(classes = UserApplyEvent.class, fallbackExecution = true)
    public void notifyFriend(UserApplyEvent event) {
        UserApply userApply = event.getUserApply();
        Integer unReadCount = userApplyDao.getUnReadCount(userApply.getTargetId());
        pushService.sendPushMsg(WSAdapter.buildApplySend(new WSFriendApply(userApply.getUid(), unReadCount)), userApply.getTargetId());
    }

}
