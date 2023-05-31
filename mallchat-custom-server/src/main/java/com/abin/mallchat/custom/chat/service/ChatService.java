package com.abin.mallchat.custom.chat.service;

import com.abin.mallchat.common.chat.domain.entity.Message;
import com.abin.mallchat.common.common.domain.vo.request.CursorPageBaseReq;
import com.abin.mallchat.common.common.domain.vo.response.CursorPageBaseResp;
import com.abin.mallchat.custom.chat.domain.vo.request.ChatMessageMarkReq;
import com.abin.mallchat.custom.chat.domain.vo.request.ChatMessagePageReq;
import com.abin.mallchat.custom.chat.domain.vo.request.ChatMessageReq;
import com.abin.mallchat.custom.chat.domain.vo.response.ChatMemberResp;
import com.abin.mallchat.custom.chat.domain.vo.response.ChatMemberStatisticResp;
import com.abin.mallchat.custom.chat.domain.vo.response.ChatMessageResp;
import com.abin.mallchat.custom.chat.domain.vo.response.ChatRoomResp;

import javax.annotation.Nullable;

/**
 * <p>
 * 消息处理类
 * </p>
 *
 * @author <a href="https://github.com/zongzibinbin">abin</a>
 * @since 2023-03-26
 */
public interface ChatService {

    /**
     * 发送消息
     *
     * @param request 消息发送请求体
     */
    Long sendMsg(ChatMessageReq request, Long uid);

    /**
     * 根据消息获取消息前端展示的物料
     *
     * @param message    消息发送请求体
     * @param receiveUid 接受消息的uid，可null
     * @return 消息
     */
    ChatMessageResp getMsgResp(Message message, Long receiveUid);

    /**
     * 根据消息获取消息前端展示的物料
     *
     * @param msgId      消息id
     * @param receiveUid 接受消息的uid，可null
     * @return 消息
     */
    ChatMessageResp getMsgResp(Long msgId, Long receiveUid);

    /**
     * 获取群成员列表
     *
     * @param request 游标翻页请求
     * @return 游标翻页返回
     */
    CursorPageBaseResp<ChatMemberResp> getMemberPage(CursorPageBaseReq request);

    /**
     * 获取消息列表
     *
     * @param request 消息列表请求
     * @return 游标翻页返回
     */
    CursorPageBaseResp<ChatMessageResp> getMsgPage(ChatMessagePageReq request, @Nullable Long receiveUid);

    /**
     * 获取会话列表
     *
     * @param request 游标翻页请求
     * @param uid     用户id
     * @return 游标翻页返回
     */
    CursorPageBaseResp<ChatRoomResp> getRoomPage(CursorPageBaseReq request, Long uid);

    ChatMemberStatisticResp getMemberStatistic();

    void setMsgMark(Long uid, ChatMessageMarkReq request);

}
