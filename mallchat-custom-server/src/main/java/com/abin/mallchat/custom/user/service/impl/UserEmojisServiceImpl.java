package com.abin.mallchat.custom.user.service.impl;

import com.abin.mallchat.common.chat.domain.entity.UserEmojis;
import com.abin.mallchat.common.common.annotation.RedissonLock;
import com.abin.mallchat.common.common.domain.vo.request.CursorPageBaseReq;
import com.abin.mallchat.common.common.domain.vo.response.ApiResult;
import com.abin.mallchat.common.common.domain.vo.response.CursorPageBaseResp;
import com.abin.mallchat.common.common.utils.CursorUtils;
import com.abin.mallchat.common.common.utils.RequestHolder;
import com.abin.mallchat.common.user.mapper.UserEmojisMapper;
import com.abin.mallchat.custom.user.service.UserEmojisService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 用户表情包 ServiceImpl
 *
 * @author: WuShiJie
 * @createTime: 2023/7/3 14:23
 */
@Service
@Slf4j
public class UserEmojisServiceImpl extends ServiceImpl<UserEmojisMapper, UserEmojis> implements UserEmojisService {

    /**
     * 游标分页工具类
     */
    @Resource
    private CursorUtils cursorUtils;

    /**
     * 表情包列表
     *
     * @param request 游标翻页请求参数
     * @return 表情包列表
     * @author WuShiJie
     * @createTime 2023/7/3 14:46
     **/
    @Override
    public CursorPageBaseResp<UserEmojis> getEmojisPage(CursorPageBaseReq request, Long uid) {
        CursorPageBaseResp<UserEmojis> cursorPageByMysql = cursorUtils.getCursorPageByMysql(this, request, wrapper -> {
            wrapper.eq(UserEmojis::getUid, uid);
        }, UserEmojis::getId);
        return cursorPageByMysql;
    }


    /**
     * 新增表情包
     *
     * @param emojis 用户表情包
     * @param uid 用户ID
     * @return 表情包
     * @author WuShiJie
     * @createTime 2023/7/3 14:46
     **/
    @Override
    @RedissonLock(key = "#uid")
    public ApiResult<UserEmojis> insertEmojis(UserEmojis emojis, Long uid) {
        //校验表情数量是否超过30
        LambdaQueryWrapper<UserEmojis> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserEmojis::getUid,uid);
        int count = this.count(queryWrapper);
        if (count>30){
            return ApiResult.fail(-1,"最多只能添加30个表情");
        }
        //校验表情是否存在
        queryWrapper.eq(UserEmojis::getExpressionUrl,emojis.getExpressionUrl());
        count = this.count(queryWrapper);
        if (count >0){
            return ApiResult.fail(-1,"当前表情已存在");
        }
        emojis.setUid(RequestHolder.get().getUid());
        this.saveOrUpdate(emojis, queryWrapper);
        return ApiResult.success(emojis);
    }
}
