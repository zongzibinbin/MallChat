package com.abin.mallchat.custom.user.service.impl;

import cn.hutool.core.util.StrUtil;
import com.abin.mallchat.common.common.constant.RedisKey;
import com.abin.mallchat.common.common.utils.JwtUtils;
import com.abin.mallchat.common.common.utils.RedisUtils;
import com.abin.mallchat.custom.user.service.LoginService;
import com.auth0.jwt.interfaces.Claim;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * Description: 登录相关处理类
 * Author: <a href="https://github.com/zongzibinbin">abin</a>
 * Date: 2023-03-19
 */
@Service
@Slf4j
public class LoginServiceImpl implements LoginService {

    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private RedisUtils redisUtils;
    //token过期时间
    private static final Integer TOKEN_EXPIRE_DAYS = 5;
    //token续期时间
    private static final Integer TOKEN_RENEWAL_DAYS = 2;

    /**
     * 校验token是不是有效
     *
     * @param token
     * @return
     */
    public boolean verify(String token) {
        Long uid = jwtUtils.getUidOrNull(token);
        if (Objects.isNull(uid)) {
            return false;
        }
        String key = RedisKey.getKey(RedisKey.USER_TOKEN_STRING, uid);
        String realToken = redisUtils.getStr(key);
        return token.equals(realToken);//有可能token失效了，需要校验是不是和最新token一致
    }

    @Async
    public void renewalTokenIfNecessary(String token) {
        Long uid = jwtUtils.getUidOrNull(token);
        if (Objects.isNull(uid)) {
            return;
        }
        String key = RedisKey.getKey(RedisKey.USER_TOKEN_STRING, uid);
        long expireDays = redisUtils.getExpire(key, TimeUnit.DAYS);
        if (expireDays == -2) {//不存在的key
            return;
        }
        if (expireDays < TOKEN_RENEWAL_DAYS) {//小于一天的token帮忙续期
            redisUtils.expire(key, TOKEN_EXPIRE_DAYS, TimeUnit.DAYS);
        }
    }

    @Override
    public String login(Long uid) {
        //获取用户token
        String token = jwtUtils.createToken(uid);
        //存储到redis
        String key = RedisKey.getKey(RedisKey.USER_TOKEN_STRING, uid);
        redisUtils.set(key, token, TOKEN_EXPIRE_DAYS, TimeUnit.DAYS);//token过期用redis中心化控制，初期采用5天过期，剩1天自动续期的方案。后续可以用双token实现
        return token;
    }

    @Override
    public Long getValidUid(String token) {
        return jwtUtils.getUidOrNull(token);
    }
}
