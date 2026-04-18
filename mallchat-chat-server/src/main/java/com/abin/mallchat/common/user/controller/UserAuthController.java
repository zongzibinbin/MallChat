package com.abin.mallchat.common.user.controller;

import com.abin.mallchat.common.common.domain.vo.response.ApiResult;
import com.abin.mallchat.common.user.domain.vo.request.auth.LoginReq;
import com.abin.mallchat.common.user.domain.vo.request.auth.RegisterReq;
import com.abin.mallchat.common.user.domain.vo.response.auth.LoginResp;
import com.abin.mallchat.common.user.service.UserAuthService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * Description: 用户认证相关接口
 * Author: <a href="https://github.com/zongzibinbin">abin</a>
 * Date: 2026-04-18
 */
@RestController
@RequestMapping("/capi/user/public/auth")
@Api(tags = "用户认证相关接口")
public class UserAuthController {

    @Autowired
    private UserAuthService userAuthService;

    @PostMapping("/register")
    @ApiOperation("用户名密码注册")
    public ApiResult<Void> register(@Valid @RequestBody RegisterReq req) {
        userAuthService.register(req);
        return ApiResult.success();
    }

    @PostMapping("/login")
    @ApiOperation("用户名密码登录")
    public ApiResult<LoginResp> login(@Valid @RequestBody LoginReq req) {
        return ApiResult.success(userAuthService.login(req));
    }
}
