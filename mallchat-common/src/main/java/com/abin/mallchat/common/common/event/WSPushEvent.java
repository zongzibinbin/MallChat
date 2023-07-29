package com.abin.mallchat.common.common.event;

import com.abin.mallchat.common.user.domain.enums.WSBaseResp;
import com.abin.mallchat.common.user.domain.enums.WSPushTypeEnum;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
public class WSPushEvent extends ApplicationEvent {
    /**
     * 推送的ws消息
     */
    private final WSBaseResp<?> wsBaseMsg;
    /**
     * 推送的uid
     */
    private final List<Long> uidList;

    /**
     * 推送类型 1个人 2全员
     *
     * @see com.abin.mallchat.common.user.domain.enums.WSPushTypeEnum
     */
    private final Integer pushType;

    public WSPushEvent(Object source, Long uid, WSBaseResp<?> wsBaseMsg) {
        super(source);
        this.uidList = Collections.singletonList(uid);
        this.wsBaseMsg = wsBaseMsg;
        this.pushType = WSPushTypeEnum.USER.getType();
    }

    public WSPushEvent(Object source, List<Long> uidList, WSBaseResp<?> wsBaseMsg) {
        super(source);
        this.uidList = uidList;
        this.wsBaseMsg = wsBaseMsg;
        this.pushType = WSPushTypeEnum.USER.getType();
    }

    public WSPushEvent(Object source, WSBaseResp<?> wsBaseMsg) {
        super(source);
        this.uidList = new ArrayList<>();
        this.wsBaseMsg = wsBaseMsg;
        this.pushType = WSPushTypeEnum.ALL.getType();
    }
}
