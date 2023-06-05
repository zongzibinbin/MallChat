package com.abin.mallchat.custom.user.service.impl;

import cn.hutool.core.util.StrUtil;
import com.abin.mallchat.common.common.event.UserBlackEvent;
import com.abin.mallchat.common.common.event.UserRegisterEvent;
import com.abin.mallchat.common.common.utils.AssertUtil;
import com.abin.mallchat.common.user.dao.BlackDao;
import com.abin.mallchat.common.user.dao.ItemConfigDao;
import com.abin.mallchat.common.user.dao.UserBackpackDao;
import com.abin.mallchat.common.user.dao.UserDao;
import com.abin.mallchat.common.user.domain.entity.Black;
import com.abin.mallchat.common.user.domain.entity.ItemConfig;
import com.abin.mallchat.common.user.domain.entity.User;
import com.abin.mallchat.common.user.domain.entity.UserBackpack;
import com.abin.mallchat.common.user.domain.enums.BlackTypeEnum;
import com.abin.mallchat.common.user.domain.enums.ItemEnum;
import com.abin.mallchat.common.user.domain.enums.ItemTypeEnum;
import com.abin.mallchat.common.user.service.cache.ItemCache;
import com.abin.mallchat.common.user.service.cache.UserCache;
import com.abin.mallchat.custom.user.domain.vo.request.user.BlackReq;
import com.abin.mallchat.custom.user.domain.vo.request.user.ModifyNameReq;
import com.abin.mallchat.custom.user.domain.vo.request.user.WearingBadgeReq;
import com.abin.mallchat.custom.user.domain.vo.response.user.BadgeResp;
import com.abin.mallchat.custom.user.domain.vo.response.user.UserInfoResp;
import com.abin.mallchat.custom.user.service.UserService;
import com.abin.mallchat.custom.user.service.adapter.UserAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Description: 用户基础操作类
 * Author: <a href="https://github.com/zongzibinbin">abin</a>
 * Date: 2023-03-19
 */
@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    private UserCache userCache;
    @Autowired
    private UserBackpackDao userBackpackDao;
    @Autowired
    private UserDao userDao;
    @Autowired
    private ItemConfigDao itemConfigDao;
    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;
    @Autowired
    private ItemCache itemCache;
    @Autowired
    private BlackDao blackDao;

    @Override
    public UserInfoResp getUserInfo(Long uid) {
        User userInfo = userCache.getUserInfo(uid);
        Integer countByValidItemId = userBackpackDao.getCountByValidItemId(uid, ItemEnum.MODIFY_NAME_CARD.getId());
        return UserAdapter.buildUserInfoResp(userInfo, countByValidItemId);
    }

    @Override
    @Transactional
    public void modifyName(Long uid, ModifyNameReq req) {
        //判断名字是不是重复
        User oldUser = userDao.getByName(req.getName());
        AssertUtil.isEmpty(oldUser, "名字已经被抢占了，请换一个哦~~");
        //判断改名卡够不够
        UserBackpack firstValidItem = userBackpackDao.getFirstValidItem(uid, ItemEnum.MODIFY_NAME_CARD.getId());
        AssertUtil.isNotEmpty(firstValidItem, "改名次数不够了，等后续活动送改名卡哦");
        //使用改名卡
        boolean useSuccess = userBackpackDao.invalidItem(firstValidItem.getId());
        if (useSuccess) {//用乐观锁，就不用分布式锁了
            //改名
            userDao.modifyName(uid, req.getName());
            //删除缓存
            userCache.delUserInfo(uid);
        }
    }

    @Override
    public List<BadgeResp> badges(Long uid) {
        //查询所有徽章
        List<ItemConfig> itemConfigs = itemCache.getByType(ItemTypeEnum.BADGE.getType());
        //查询用户拥有的徽章
        List<UserBackpack> backpacks = userBackpackDao.getByItemIds(uid, itemConfigs.stream().map(ItemConfig::getId).collect(Collectors.toList()));
        //查询用户当前佩戴的标签
        User user = userDao.getById(uid);
        return UserAdapter.buildBadgeResp(itemConfigs, backpacks, user);
    }

    @Override
    public void wearingBadge(Long uid, WearingBadgeReq req) {
        //确保有这个徽章
        UserBackpack firstValidItem = userBackpackDao.getFirstValidItem(uid, req.getBadgeId());
        AssertUtil.isNotEmpty(firstValidItem, "您没有这个徽章哦，快去达成条件获取吧");
        //确保物品类型是徽章
        ItemConfig itemConfig = itemConfigDao.getById(firstValidItem.getItemId());
        AssertUtil.equal(itemConfig.getType(), ItemTypeEnum.BADGE.getType(), "该徽章不可佩戴");
        //佩戴徽章
        userDao.wearingBadge(uid, req.getBadgeId());
        //删除用户缓存
        userCache.delUserInfo(uid);
    }

    @Override
    public void register(String openId) {
        User insert = User.builder().openId(openId).build();
        userDao.save(insert);
        applicationEventPublisher.publishEvent(new UserRegisterEvent(this, insert));
    }

    @Override
    public void black(BlackReq req) {
        Long uid = req.getUid();
        Black user = new Black();
        user.setTarget(uid.toString());
        user.setType(BlackTypeEnum.UID.getType());
        blackDao.save(user);
        User byId = userDao.getById(uid);
        blackIp(byId.getIpInfo().getCreateIp());
        blackIp(byId.getIpInfo().getUpdateIp());
        applicationEventPublisher.publishEvent(new UserBlackEvent(this, byId));
    }

    private void blackIp(String ip) {
        if (StrUtil.isBlank(ip)) {
            return;
        }
        try {
            Black user = new Black();
            user.setTarget(ip);
            user.setType(BlackTypeEnum.IP.getType());
            blackDao.save(user);
        } catch (Exception e) {
            log.error("duplicate black ip:{}", ip);
        }
    }
}
