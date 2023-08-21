package com.abin.mallchat.common.chat.service.strategy.msg;

import com.abin.mallchat.common.chat.domain.entity.Message;

public interface MsgHandler<RESP, REQ> {

    void saveMsg(Message msg, REQ req);

    RESP showMsg(Message msg);

}
