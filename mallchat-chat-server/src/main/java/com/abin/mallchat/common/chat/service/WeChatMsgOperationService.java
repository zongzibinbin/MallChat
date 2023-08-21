package com.abin.mallchat.common.chat.service;

import java.util.List;

public interface WeChatMsgOperationService {
    /**
     * 向被at的用户微信推送群聊消息
     *
     * @param senderUid senderUid
     * @param receiverUidList receiverUidList
     * @param msg msg
     */
    void publishChatMsgToWeChatUser(long senderUid, List<Long> receiverUidList, String msg);
}
