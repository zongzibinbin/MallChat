package com.abin.mallchat.common.user.service.cache;

import cn.hutool.core.lang.Pair;
import com.abin.mallchat.common.common.constant.RedisKey;
import com.abin.mallchat.common.common.domain.vo.request.CursorPageBaseReq;
import com.abin.mallchat.common.common.domain.vo.response.CursorPageBaseResp;
import com.abin.mallchat.common.common.utils.CursorUtils;
import com.abin.mallchat.common.common.utils.RedisUtils;
import com.abin.mallchat.common.user.dao.BlackDao;
import com.abin.mallchat.common.user.dao.RoleDao;
import com.abin.mallchat.common.user.dao.UserRoleDao;
import com.abin.mallchat.common.user.domain.entity.Black;
import com.abin.mallchat.common.user.domain.entity.User;
import com.abin.mallchat.common.user.domain.entity.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Description: 用户相关缓存
 * Author: <a href="https://github.com/zongzibinbin">abin</a>
 * Date: 2023-03-27
 */
@Component
public class UserCache {

    @Autowired
    private UserInfoCache userInfoCache;
    @Autowired
    private BlackDao blackDao;
    @Autowired
    private RoleDao roleDao;
    @Autowired
    private UserRoleDao userRoleDao;
    @Autowired
    private UserSummaryCache userSummaryCache;

    public Long getOnlineNum() {
        String onlineKey = RedisKey.getKey(RedisKey.ONLINE_UID_ZET);
        return RedisUtils.zCard(onlineKey);
    }

    public Long getOfflineNum() {
        String offlineKey = RedisKey.getKey(RedisKey.OFFLINE_UID_ZET);
        return RedisUtils.zCard(offlineKey);
    }

    //移除用户
    public void remove(Long uid) {
        String onlineKey = RedisKey.getKey(RedisKey.ONLINE_UID_ZET);
        String offlineKey = RedisKey.getKey(RedisKey.OFFLINE_UID_ZET);
        //移除离线表
        RedisUtils.zRemove(offlineKey, uid);
        //移除上线表
        RedisUtils.zRemove(onlineKey, uid);
    }

    //用户上线
    public void online(Long uid, Date optTime) {
        String onlineKey = RedisKey.getKey(RedisKey.ONLINE_UID_ZET);
        String offlineKey = RedisKey.getKey(RedisKey.OFFLINE_UID_ZET);
        //移除离线表
        RedisUtils.zRemove(offlineKey, uid);
        //更新上线表
        RedisUtils.zAdd(onlineKey, uid, optTime.getTime());
    }

    //获取用户上线列表
    public List<Long> getOnlineUidList() {
        String onlineKey = RedisKey.getKey(RedisKey.ONLINE_UID_ZET);
        Set<String> strings = RedisUtils.zAll(onlineKey);
        return strings.stream().map(Long::parseLong).collect(Collectors.toList());
    }

    public boolean isOnline(Long uid) {
        String onlineKey = RedisKey.getKey(RedisKey.ONLINE_UID_ZET);
        return RedisUtils.zIsMember(onlineKey, uid);
    }

    //用户下线
    public void offline(Long uid, Date optTime) {
        String onlineKey = RedisKey.getKey(RedisKey.ONLINE_UID_ZET);
        String offlineKey = RedisKey.getKey(RedisKey.OFFLINE_UID_ZET);
        //移除上线线表
        RedisUtils.zRemove(onlineKey, uid);
        //更新上线表
        RedisUtils.zAdd(offlineKey, uid, optTime.getTime());
    }

    public CursorPageBaseResp<Pair<Long, Double>> getOnlineCursorPage(CursorPageBaseReq pageBaseReq) {
        return CursorUtils.getCursorPageByRedis(pageBaseReq, RedisKey.getKey(RedisKey.ONLINE_UID_ZET), Long::parseLong);
    }

    public CursorPageBaseResp<Pair<Long, Double>> getOfflineCursorPage(CursorPageBaseReq pageBaseReq) {
        return CursorUtils.getCursorPageByRedis(pageBaseReq, RedisKey.getKey(RedisKey.OFFLINE_UID_ZET), Long::parseLong);
    }

    public List<Long> getUserModifyTime(List<Long> uidList) {
        List<String> keys = uidList.stream().map(uid -> RedisKey.getKey(RedisKey.USER_MODIFY_STRING, uid)).collect(Collectors.toList());
        return RedisUtils.mget(keys, Long.class);
    }

    public void refreshUserModifyTime(Long uid) {
        String key = RedisKey.getKey(RedisKey.USER_MODIFY_STRING, uid);
        RedisUtils.set(key, new Date().getTime());
    }

    /**
     * 获取用户信息，盘路缓存模式
     */
    public User getUserInfo(Long uid) {
        return userInfoCache.get(uid);
    }

    /**
     * 获取用户信息，盘路缓存模式
     */
    public Map<Long, User> getUserInfoBatch(Set<Long> uids) {
        return userInfoCache.getBatch(new ArrayList<>(uids));
    }

    public void userInfoChange(Long uid) {
        delUserInfo(uid);
        //删除UserSummaryCache，前端下次懒加载的时候可以获取到最新的数据
        userSummaryCache.delete(uid);
        refreshUserModifyTime(uid);
    }

    public void delUserInfo(Long uid) {
        userInfoCache.delete(uid);
    }

    @Cacheable(cacheNames = "user", key = "'blackList'")
    public Map<Integer, Set<String>> getBlackMap() {
        Map<Integer, List<Black>> collect = blackDao.list().stream().collect(Collectors.groupingBy(Black::getType));
        Map<Integer, Set<String>> result = new HashMap<>(collect.size());
        for (Map.Entry<Integer, List<Black>> entry : collect.entrySet()) {
            result.put(entry.getKey(), entry.getValue().stream().map(Black::getTarget).collect(Collectors.toSet()));
        }
        return result;
    }

    @CacheEvict(cacheNames = "user", key = "'blackList'")
    public Map<Integer, Set<String>> evictBlackMap() {
        return null;
    }

    @Cacheable(cacheNames = "user", key = "'roles'+#uid")
    public Set<Long> getRoleSet(Long uid) {
        List<UserRole> userRoles = userRoleDao.listByUid(uid);
        return userRoles.stream()
                .map(UserRole::getRoleId)
                .collect(Collectors.toSet());
    }
}
