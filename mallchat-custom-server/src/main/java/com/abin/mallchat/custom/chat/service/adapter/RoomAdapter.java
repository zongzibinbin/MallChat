package com.abin.mallchat.custom.chat.service.adapter;

import cn.hutool.core.bean.BeanUtil;
import com.abin.mallchat.common.chat.domain.entity.Room;
import com.abin.mallchat.custom.chat.domain.vo.response.ChatRoomResp;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 消息适配器
 * </p>
 *
 * @author <a href="https://github.com/zongzibinbin">abin</a>
 * @since 2023-03-26
 */
public class RoomAdapter {

    public static List<ChatRoomResp> buildResp(List<Room> list) {
        return list.stream()
                .map(a -> {
                    ChatRoomResp resp = new ChatRoomResp();
                    BeanUtil.copyProperties(a, resp);
                    resp.setLastActiveTime(a.getActiveTime());
                    return resp;
                }).collect(Collectors.toList());
    }

}
