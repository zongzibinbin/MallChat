package com.abin.mallchat.common.user.service.adapter;

import com.abin.mallchat.common.user.domain.entity.User;
import com.abin.mallchat.common.user.domain.entity.UserApply;
import com.abin.mallchat.common.user.domain.entity.UserFriend;
import com.abin.mallchat.common.user.domain.vo.request.friend.FriendApplyReq;
import com.abin.mallchat.common.user.domain.vo.response.friend.FriendApplyResp;
import com.abin.mallchat.common.user.domain.vo.response.friend.FriendResp;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.abin.mallchat.common.user.domain.enums.ApplyReadStatusEnum.UNREAD;
import static com.abin.mallchat.common.user.domain.enums.ApplyStatusEnum.WAIT_APPROVAL;
import static com.abin.mallchat.common.user.domain.enums.ApplyTypeEnum.ADD_FRIEND;

/**
 * Description: 好友适配器
 * Author: <a href="https://github.com/zongzibinbin">abin</a>
 * Date: 2023-07-22
 */
public class FriendAdapter {


    public static UserApply buildFriendApply(Long uid, FriendApplyReq request) {
        UserApply userApplyNew = new UserApply();
        userApplyNew.setUid(uid);
        userApplyNew.setMsg(request.getMsg());
        userApplyNew.setType(ADD_FRIEND.getCode());
        userApplyNew.setTargetId(request.getTargetUid());
        userApplyNew.setStatus(WAIT_APPROVAL.getCode());
        userApplyNew.setReadStatus(UNREAD.getCode());
        return userApplyNew;
    }

    public static List<FriendApplyResp> buildFriendApplyList(List<UserApply> records) {
        return records.stream().map(userApply -> {
            FriendApplyResp friendApplyResp = new FriendApplyResp();
            friendApplyResp.setUid(userApply.getUid());
            friendApplyResp.setType(userApply.getType());
            friendApplyResp.setApplyId(userApply.getId());
            friendApplyResp.setMsg(userApply.getMsg());
            friendApplyResp.setStatus(userApply.getStatus());
            return friendApplyResp;
        }).collect(Collectors.toList());
    }

    public static List<FriendResp> buildFriend(List<UserFriend> list, List<User> userList) {
        Map<Long, User> userMap = userList.stream().collect(Collectors.toMap(User::getId, user -> user));
        return list.stream().map(userFriend -> {
            FriendResp resp = new FriendResp();
            resp.setUid(userFriend.getFriendUid());
            User user = userMap.get(userFriend.getFriendUid());
            if (Objects.nonNull(user)) {
                resp.setActiveStatus(user.getActiveStatus());
            }
            return resp;
        }).collect(Collectors.toList());
    }
}
