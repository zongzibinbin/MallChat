package com.abin.mallchat.custom.user.service;

import com.abin.mallchat.common.chat.domain.entity.UserEmojis;
import com.abin.mallchat.common.common.domain.vo.request.CursorPageBaseReq;
import com.abin.mallchat.common.common.domain.vo.response.ApiResult;
import com.abin.mallchat.common.common.domain.vo.response.CursorPageBaseResp;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 用户表情包 Service
 *
 * @author: WuShiJie
 * @createTime: 2023/7/3 14:22
 */
public interface UserEmojisService extends IService<UserEmojis> {

    /**
     * 表情包列表
     *
     * @param request 游标翻页请求参数
     * @return 表情包列表
     * @author WuShiJie
     * @createTime 2023/7/3 14:46
     **/
    CursorPageBaseResp<UserEmojis> getEmojisPage(CursorPageBaseReq request, Long uid);

    /**
     * 新增表情包
     *
     * @param emojis 用户表情包
     * @param uid 用户ID
     * @return 表情包
     * @author WuShiJie
     * @createTime 2023/7/3 14:46
     **/
    ApiResult<UserEmojis> insertEmojis(UserEmojis emojis, Long uid);
}
