package com.abin.mallchat.custom.chat.service.adapter;

import cn.hutool.core.lang.Pair;
import com.abin.mallchat.common.user.domain.entity.User;
import com.abin.mallchat.common.user.domain.entity.UserFriend;
import com.abin.mallchat.common.user.domain.enums.ChatActiveStatusEnum;
import com.abin.mallchat.common.user.service.cache.UserCache;
import com.abin.mallchat.custom.chat.domain.vo.response.ChatMemberResp;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Description: 成员适配器
 * Author: <a href="https://github.com/zongzibinbin">abin</a>
 * Date: 2023-03-26
 */
@Component
@Slf4j
public class MemberAdapter {
    @Autowired
    private UserCache userCache;

    public List<ChatMemberResp> buildMember(List<Pair<Long, Double>> list, ChatActiveStatusEnum statusEnum) {
        return list.stream().map(a -> {
            ChatMemberResp resp = new ChatMemberResp();
            resp.setActiveStatus(statusEnum.getStatus());
            resp.setLastOptTime(new Date(a.getValue().longValue()));
            resp.setUid(a.getKey());
            return resp;
        }).collect(Collectors.toList());
    }

    public static List<ChatMemberResp> buildMember(List<UserFriend> list, List<User> userList) {
        Map<Long, User> userMap = userList.stream().collect(Collectors.toMap(User::getId, user -> user));
        return list.stream().map(userFriend -> {
            ChatMemberResp resp = new ChatMemberResp();
            resp.setUid(userFriend.getFriendUid());
            User user = userMap.get(userFriend.getFriendUid());
            if (Objects.nonNull(user)) {
                resp.setActiveStatus(user.getActiveStatus());
                resp.setLastOptTime(user.getLastOptTime());
            }
            return resp;
        }).collect(Collectors.toList());
    }
}
