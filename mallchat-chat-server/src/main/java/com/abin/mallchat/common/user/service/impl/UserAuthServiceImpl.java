package com.abin.mallchat.common.user.service.impl;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.abin.mallchat.common.common.algorithm.sensitiveWord.SensitiveWordBs;
import com.abin.mallchat.common.common.domain.enums.NormalOrNoEnum;
import com.abin.mallchat.common.common.utils.AssertUtil;
import com.abin.mallchat.common.user.dao.UserDao;
import com.abin.mallchat.common.user.domain.entity.User;
import com.abin.mallchat.common.user.domain.enums.RoleEnum;
import com.abin.mallchat.common.user.domain.vo.request.auth.LoginReq;
import com.abin.mallchat.common.user.domain.vo.request.auth.RegisterReq;
import com.abin.mallchat.common.user.domain.vo.response.auth.LoginResp;
import com.abin.mallchat.common.user.service.IRoleService;
import com.abin.mallchat.common.user.service.LoginService;
import com.abin.mallchat.common.user.service.UserAuthService;
import com.abin.mallchat.common.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

/**
 * Description: 用户认证相关业务
 * Author: <a href="https://github.com/zongzibinbin">abin</a>
 * Date: 2026-04-18
 */
@Service
public class UserAuthServiceImpl implements UserAuthService {

    private static final int DEFAULT_NAME_RETRY_TIMES = 5;
    private static final String DEFAULT_NAME_PREFIX = "用户";
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    @Autowired
    private UserDao userDao;
    @Autowired
    private UserService userService;
    @Autowired
    private LoginService loginService;
    @Autowired
    private IRoleService iRoleService;
    @Autowired
    private SensitiveWordBs sensitiveWordBs;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void register(RegisterReq req) {
        String username = StrUtil.trim(req.getUsername());
        String nickname = StrUtil.trim(req.getNickname());
        AssertUtil.isEmpty(userDao.getByUsername(username), "账号已存在，请换一个试试哦");
        String passwordHash = passwordEncoder.encode(req.getPassword());
        boolean autoGenerateName = StrUtil.isBlank(nickname);

        if (!autoGenerateName) {
            AssertUtil.isFalse(sensitiveWordBs.hasSensitiveWord(nickname), "昵称中包含敏感词，请重新输入");
            AssertUtil.isEmpty(userDao.getByName(nickname), "昵称已经被抢占了，请换一个哦~~");
        }

        for (int i = 0; i < DEFAULT_NAME_RETRY_TIMES; i++) {
            String registerName = autoGenerateName ? generateRegisterName() : nickname;
            if (autoGenerateName && Objects.nonNull(userDao.getByName(registerName))) {
                continue;
            }
            User user = User.builder()
                    .username(username)
                    .passwordHash(passwordHash)
                    .name(registerName)
                    .openId(generateOpenId(username))
                    .build();
            try {
                userService.register(user);
                return;
            } catch (DuplicateKeyException e) {
                if (Objects.nonNull(userDao.getByUsername(username))) {
                    AssertUtil.isTrue(false, "账号已存在，请换一个试试哦");
                }
                if (!autoGenerateName && Objects.nonNull(userDao.getByName(registerName))) {
                    AssertUtil.isTrue(false, "昵称已经被抢占了，请换一个哦~~");
                }
            }
        }
        AssertUtil.isTrue(false, "注册失败，请稍后再试哦~~");
    }

    @Override
    public LoginResp login(LoginReq req) {
        String username = StrUtil.trim(req.getUsername());
        User user = userDao.getByUsername(username);
        AssertUtil.isNotEmpty(user, "账号或密码错误");
        AssertUtil.equal(user.getStatus(), NormalOrNoEnum.NORMAL.getStatus(), "账号已被封禁");
        AssertUtil.isTrue(passwordEncoder.matches(req.getPassword(), user.getPasswordHash()), "账号或密码错误");
        String token = loginService.login(user.getId());
        boolean hasPower = iRoleService.hasPower(user.getId(), RoleEnum.CHAT_MANAGER);
        return LoginResp.builder()
                .uid(user.getId())
                .avatar(user.getAvatar())
                .token(token)
                .name(user.getName())
                .power(hasPower ? 1 : 0)
                .build();
    }

    private String generateRegisterName() {
        return DEFAULT_NAME_PREFIX + RandomUtil.randomNumbers(4);
    }

    private String generateOpenId(String username) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            byte[] digest = messageDigest.digest(("pwd:" + username).getBytes(StandardCharsets.UTF_8));
            StringBuilder builder = new StringBuilder();
            for (byte b : digest) {
                builder.append(String.format("%02x", b));
            }
            return builder.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("md5 algorithm not found", e);
        }
    }
}
