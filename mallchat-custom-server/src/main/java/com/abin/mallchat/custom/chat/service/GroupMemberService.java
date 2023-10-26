package com.abin.mallchat.custom.chat.service;

import com.abin.mallchat.custom.chat.domain.vo.request.AdminAddReq;

/**
 * @Author Kkuil
 * @Date 2023/10/24 15:45
 * @Description 群成员服务接口
 */
public interface GroupMemberService {
    /**
     * 增加管理员
     *
     * @param uid     用户ID
     * @param request 请求信息
     */
    void addAdmin(Long uid, AdminAddReq request);
}
