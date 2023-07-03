package com.abin.mallchat.custom.user.service.impl;

import com.abin.mallchat.common.chat.domain.entity.McEmojis;
import com.abin.mallchat.common.common.domain.vo.request.CursorPageBaseReq;
import com.abin.mallchat.common.common.domain.vo.response.CursorPageBaseResp;
import com.abin.mallchat.common.common.utils.CursorUtils;
import com.abin.mallchat.common.user.mapper.EmojisMapper;
import com.abin.mallchat.custom.chat.service.adapter.RoomAdapter;
import com.abin.mallchat.custom.user.service.EmojisService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 用户表情包 ServiceImpl
 *
 * @author: WuShiJie
 * @createTime: 2023/7/3 14:23
 */
@Service
@Slf4j
public class EmojisServiceImpl extends ServiceImpl<EmojisMapper, McEmojis> implements EmojisService {

    @Autowired
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
    public CursorPageBaseResp<McEmojis> getEmojisPage(CursorPageBaseReq request, Long uid) {
        CursorPageBaseResp<McEmojis> cursorPageByMysql = cursorUtils.getCursorPageByMysql(this, request, wrapper -> {
            wrapper.eq(McEmojis::getUserId, uid);
        }, McEmojis::getId);
        return cursorPageByMysql;
    }
}
