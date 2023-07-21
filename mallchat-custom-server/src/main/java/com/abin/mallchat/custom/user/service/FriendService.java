package com.abin.mallchat.custom.user.service;

import com.abin.mallchat.common.common.domain.vo.request.PageBaseReq;
import com.abin.mallchat.common.common.domain.vo.response.PageBaseResp;
import com.abin.mallchat.custom.user.domain.vo.request.friend.FriendApplyReq;
import com.abin.mallchat.custom.user.domain.vo.request.friend.FriendApproveReq;
import com.abin.mallchat.custom.user.domain.vo.request.friend.FriendCheckReq;
import com.abin.mallchat.custom.user.domain.vo.response.friend.FriendApplyResp;
import com.abin.mallchat.custom.user.domain.vo.response.friend.FriendCheckResp;
import com.abin.mallchat.custom.user.domain.vo.response.friend.FriendUnreadResp;

/**
 * @author : limeng
 * @description : 好友
 * @date : 2023/07/19
 */
public interface FriendService {

    /**
     * 检查
     * 检查是否是自己好友
     *
     * @param request 请求
     * @param uid     uid
     * @return {@link FriendCheckResp}
     */
    FriendCheckResp check(Long uid, FriendCheckReq request);

    /**
     * 应用
     * 申请好友
     *
     * @param request 请求
     * @param uid     uid
     */
    void apply(Long uid, FriendApplyReq request);

    /**
     * 分页查询好友申请
     *
     * @param request 请求
     * @return {@link PageBaseResp}<{@link FriendApplyResp}>
     */
    PageBaseResp<FriendApplyResp> pageApplyFriend(Long uid, PageBaseReq request);

    /**
     * 申请未读数
     *
     * @return {@link FriendUnreadResp}
     */
    FriendUnreadResp unread(Long uid);

    /**
     * 同意好友申请
     *
     * @param uid     uid
     * @param request 请求
     */
    void applyApprove(FriendApproveReq request);
}
