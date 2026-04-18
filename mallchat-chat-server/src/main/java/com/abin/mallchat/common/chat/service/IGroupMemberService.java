package com.abin.mallchat.common.chat.service;

import com.abin.mallchat.common.chat.domain.vo.request.admin.AdminAddReq;
import com.abin.mallchat.common.chat.domain.vo.request.admin.AdminRevokeReq;
import com.abin.mallchat.common.chat.domain.vo.request.member.AllMuteReq;
import com.abin.mallchat.common.chat.domain.vo.request.member.MemberExitReq;
import com.abin.mallchat.common.chat.domain.vo.request.member.MemberMuteReq;

/**
 * <p>
 * 群成员表 服务类
 * </p>
 *
 * @author <a href="https://github.com/zongzibinbin">abin</a>
 * @since 2023-07-16
 */
public interface IGroupMemberService {
    /**
     * 增加管理员
     *
     * @param uid     用户ID
     * @param request 请求信息
     */
    void addAdmin(Long uid, AdminAddReq request);

    /**
     * 撤销管理员
     *
     * @param uid     用户ID
     * @param request 请求信息
     */
    void revokeAdmin(Long uid, AdminRevokeReq request);

    /**
     * 退出群聊
     *
     * @param uid     用户ID
     * @param request 请求信息
     */
    void exitGroup(Long uid, MemberExitReq request);

    /**
     * 成员禁言/解除禁言
     *
     * @param uid     操作人用户ID
     * @param request 禁言请求信息
     */
    void muteMember(Long uid, MemberMuteReq request);

    /**
     * 全员禁言/解除全员禁言
     *
     * @param uid     操作人用户ID
     * @param request 全员禁言请求信息
     */
    void allMute(Long uid, AllMuteReq request);
}
