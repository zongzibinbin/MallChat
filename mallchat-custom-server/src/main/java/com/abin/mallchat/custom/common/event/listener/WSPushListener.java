package com.abin.mallchat.custom.common.event.listener;

import com.abin.mallchat.common.common.event.WSPushEvent;
import com.abin.mallchat.common.user.domain.enums.WSPushTypeEnum;
import com.abin.mallchat.custom.user.service.WebSocketService;
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
public class WSPushListener {
    @Autowired
    private WebSocketService webSocketService;

    @Async
    @TransactionalEventListener(classes = WSPushEvent.class, fallbackExecution = true)
    public void wsPush(WSPushEvent event) {
        WSPushTypeEnum wsPushTypeEnum = WSPushTypeEnum.of(event.getPushType());
        switch (wsPushTypeEnum) {
            case USER:
                webSocketService.sendToUidList(event.getWsBaseMsg(), event.getUidList());
                break;
            case ALL:
                webSocketService.sendToAllOnline(event.getWsBaseMsg(), null);
                break;
        }
    }

}
