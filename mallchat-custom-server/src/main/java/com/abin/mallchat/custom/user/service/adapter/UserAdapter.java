package com.abin.mallchat.custom.user.service.adapter;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.abin.mallchat.common.common.domain.enums.YesOrNoEnum;
import com.abin.mallchat.common.user.domain.entity.ItemConfig;
import com.abin.mallchat.common.user.domain.entity.User;
import com.abin.mallchat.common.user.domain.entity.UserBackpack;
import com.abin.mallchat.custom.user.domain.vo.response.user.BadgeResp;
import com.abin.mallchat.custom.user.domain.vo.response.user.UserInfoResp;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.bean.WxOAuth2UserInfo;
import me.chanjar.weixin.mp.bean.result.WxMpUser;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Description: 用户适配器
 * Author: <a href="https://github.com/zongzibinbin">abin</a>
 * Date: 2023-03-19
 */
@Slf4j
public class UserAdapter {

    public static User buildUser(String openId) {
        User user = new User();
        user.setOpenId(openId);
        return user;
    }

    public static User buildAuthorizeUser(Long id, WxOAuth2UserInfo userInfo) {
        User user = new User();
        user.setId(id);
        user.setAvatar(userInfo.getHeadImgUrl());
        user.setName(userInfo.getNickname());
        user.setSex(userInfo.getSex());
        return user;
    }

    public static UserInfoResp buildUserInfoResp(User userInfo, Integer countByValidItemId) {
        UserInfoResp userInfoResp = new UserInfoResp();
        BeanUtil.copyProperties(userInfo, userInfoResp);
        userInfoResp.setModifyNameChance(countByValidItemId);
        return userInfoResp;
    }

    public static List<BadgeResp> buildBadgeResp(List<ItemConfig> itemConfigs, List<UserBackpack> backpacks, User user) {
        Set<Long> obtainItemSet = backpacks.stream().map(UserBackpack::getItemId).collect(Collectors.toSet());
        return itemConfigs.stream().map(a -> {
            BadgeResp resp = new BadgeResp();
            BeanUtil.copyProperties(a, resp);
            resp.setObtain(obtainItemSet.contains(a.getId()) ? YesOrNoEnum.YES.getStatus() : YesOrNoEnum.NO.getStatus());
            resp.setWearing(ObjectUtil.equal(a.getId(), user.getItemId()) ? YesOrNoEnum.YES.getStatus() : YesOrNoEnum.NO.getStatus());
            return resp;
        }).collect(Collectors.toList());
    }
}
