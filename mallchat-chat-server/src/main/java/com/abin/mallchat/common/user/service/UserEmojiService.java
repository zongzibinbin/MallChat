package com.abin.mallchat.common.user.service;

import com.abin.mallchat.common.common.domain.vo.response.ApiResult;
import com.abin.mallchat.common.common.domain.vo.response.IdRespVO;
import com.abin.mallchat.common.user.domain.vo.request.user.UserEmojiReq;
import com.abin.mallchat.common.user.domain.vo.response.user.UserEmojiResp;

import java.util.List;

/**
 * 用户表情包 Service
 *
 * @author: WuShiJie
 * @createTime: 2023/7/3 14:22
 */
public interface UserEmojiService {

    /**
     * 表情包列表
     *
     * @return 表情包列表
     * @author WuShiJie
     * @createTime 2023/7/3 14:46
     **/
    List<UserEmojiResp> list(Long uid);

    /**
     * 新增表情包
     *
     * @param emojis 用户表情包
     * @param uid    用户ID
     * @return 表情包
     * @author WuShiJie
     * @createTime 2023/7/3 14:46
     **/
    ApiResult<IdRespVO> insert(UserEmojiReq emojis, Long uid);

    /**
     * 删除表情包
     *
     * @param id
     * @param uid
     */
    void remove(Long id, Long uid);
}
