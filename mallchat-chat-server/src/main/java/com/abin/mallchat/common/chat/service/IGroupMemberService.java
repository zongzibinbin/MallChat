package com.abin.mallchat.common.chat.service;

import com.abin.mallchat.common.chat.domain.vo.request.admin.AdminAddReq;
import com.abin.mallchat.common.chat.domain.vo.request.admin.AdminRevokeReq;
import com.abin.mallchat.common.chat.domain.vo.request.member.MemberExitReq;

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

    void exitGroup(Long uid, MemberExitReq request);
}
