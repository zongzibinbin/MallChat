package com.abin.mallchat.custom.user.service;

import com.abin.mallchat.common.chat.domain.entity.McEmojis;
import com.abin.mallchat.common.common.domain.vo.request.CursorPageBaseReq;
import com.abin.mallchat.common.common.domain.vo.response.CursorPageBaseResp;
import com.abin.mallchat.custom.chat.domain.vo.response.ChatRoomResp;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 用户表情包 Service
 *
 * @author: WuShiJie
 * @createTime: 2023/7/3 14:22
 */
public interface EmojisService extends IService<McEmojis> {

    /**
     * 表情包列表
     *
     * @param request 游标翻页请求参数
     * @return 表情包列表
     * @author WuShiJie
     * @createTime 2023/7/3 14:46
     **/
    CursorPageBaseResp<McEmojis> getEmojisPage(CursorPageBaseReq request, Long uid);
}
