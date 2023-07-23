package com.abin.mallchat.common.chat.service;

import com.abin.mallchat.common.chat.domain.entity.RoomFriend;

import java.util.List;

/**
 * Description: 房间底层管理
 * Author: <a href="https://github.com/zongzibinbin">abin</a>
 * Date: 2023-07-22
 */
public interface RoomService {

    /**
     * 创建一个单聊房间
     */
    RoomFriend createFriendRoom(List<Long> uidList);
}
