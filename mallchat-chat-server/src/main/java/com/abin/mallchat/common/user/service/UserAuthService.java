package com.abin.mallchat.common.user.service;

import com.abin.mallchat.common.user.domain.vo.request.auth.LoginReq;
import com.abin.mallchat.common.user.domain.vo.request.auth.RegisterReq;
import com.abin.mallchat.common.user.domain.vo.response.auth.LoginResp;

/**
 * Description: 用户认证相关接口
 * Author: <a href="https://github.com/zongzibinbin">abin</a>
 * Date: 2026-04-18
 */
public interface UserAuthService {

    /**
     * 用户名密码注册
     */
    void register(RegisterReq req);

    /**
     * 用户名密码登录
     */
    LoginResp login(LoginReq req);
}
