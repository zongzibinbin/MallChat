package com.abin.mallchat.common.user.service.cache;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Pair;
import com.abin.mallchat.common.common.constant.RedisKey;
import com.abin.mallchat.common.common.domain.vo.request.CursorPageBaseReq;
import com.abin.mallchat.common.common.domain.vo.response.CursorPageBaseResp;
import com.abin.mallchat.common.common.utils.CursorUtils;
import com.abin.mallchat.common.common.utils.RedisUtils;
import com.abin.mallchat.common.user.dao.UserDao;
import com.abin.mallchat.common.user.domain.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.security.PublicKey;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Description: 用户相关缓存
 * Author: <a href="https://github.com/zongzibinbin">abin</a>
 * Date: 2023-03-27
 */
@Component
public class UserCache {

    @Autowired
    private RedisUtils redisUtils;
    @Autowired
    private CursorUtils cursorUtils;
    @Autowired
    private UserDao userDao;

    public Long getOnlineNum() {
        String onlineKey = RedisKey.getKey(RedisKey.ONLINE_UID_ZET);
        return redisUtils.zCard(onlineKey);
    }

    public Long getOfflineNum() {
        String offlineKey = RedisKey.getKey(RedisKey.OFFLINE_UID_ZET);
        return redisUtils.zCard(offlineKey);
    }

    //用户上线
    public void online(Long uid, Date optTime) {
        String onlineKey = RedisKey.getKey(RedisKey.ONLINE_UID_ZET);
        String offlineKey = RedisKey.getKey(RedisKey.OFFLINE_UID_ZET);
        //移除离线表
        redisUtils.zRemove(offlineKey, uid);
        //更新上线表
        redisUtils.zAdd(onlineKey, uid, optTime.getTime());
    }

    //用户下线
    public void offline(Long uid, Date optTime) {
        String onlineKey = RedisKey.getKey(RedisKey.ONLINE_UID_ZET);
        String offlineKey = RedisKey.getKey(RedisKey.OFFLINE_UID_ZET);
        //移除上线线表
        redisUtils.zRemove(onlineKey, uid);
        //更新上线表
        redisUtils.zAdd(offlineKey, uid, optTime.getTime());
    }

    public CursorPageBaseResp<Pair<Long, Double>> getOnlineCursorPage(CursorPageBaseReq pageBaseReq) {
        return cursorUtils.getCursorPageByRedis(pageBaseReq, RedisKey.getKey(RedisKey.ONLINE_UID_ZET), Long::parseLong);
    }

    public CursorPageBaseResp<Pair<Long, Double>> getOfflineCursorPage(CursorPageBaseReq pageBaseReq) {
        return cursorUtils.getCursorPageByRedis(pageBaseReq, RedisKey.getKey(RedisKey.OFFLINE_UID_ZET), Long::parseLong);
    }

    /**
     * 获取用户信息，盘路缓存模式
     *
     * @param uid
     * @return
     */
    public User getUserInfo(Long uid) {//todo 后期做二级缓存
        return getUserInfoBatch(Collections.singleton(uid)).get(uid);
    }

    /**
     * 获取用户信息，盘路缓存模式
     *
     * @param uids
     * @return
     */
    public Map<Long, User> getUserInfoBatch(Set<Long> uids) {
        List<String> keys = uids.stream().map(a -> RedisKey.getKey(RedisKey.USER_INFO_STRING, a)).collect(Collectors.toList());
        List<User> mget = redisUtils.mget(keys, User.class);
        Map<Long, User> map = mget.stream().collect(Collectors.toMap(User::getId, Function.identity()));
        //还需要load更新的uid
        List<Long> needLoadUidList = uids.stream().filter(a -> !map.containsKey(a)).collect(Collectors.toList());
        if (CollUtil.isNotEmpty(needLoadUidList)) {
            List<User> needLoadUserList = userDao.listByIds(needLoadUidList);
            Map<String, User> redisMap = needLoadUserList.stream().collect(Collectors.toMap(a -> RedisKey.getKey(RedisKey.USER_INFO_STRING, a.getId()), Function.identity()));
            redisUtils.mset(redisMap, 5 * 60);
            map.putAll(needLoadUserList.stream().collect(Collectors.toMap(User::getId, Function.identity())));
        }
        return map;
    }

    public void delUserInfo(Long uid) {
        String key = RedisKey.getKey(RedisKey.USER_INFO_STRING, uid);
        redisUtils.del(key);
    }

}
