package com.abin.mallchat.common.chat.dao;

import com.abin.mallchat.common.chat.domain.entity.Message;
import com.abin.mallchat.common.chat.domain.entity.Room;
import com.abin.mallchat.common.chat.domain.enums.MessageStatusEnum;
import com.abin.mallchat.common.chat.domain.enums.RoomTypeEnum;
import com.abin.mallchat.common.chat.mapper.RoomMapper;
import com.abin.mallchat.common.common.domain.vo.request.CursorPageBaseReq;
import com.abin.mallchat.common.common.domain.vo.response.CursorPageBaseResp;
import com.abin.mallchat.common.common.utils.CursorUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 会话表 服务实现类
 * </p>
 *
 * @author <a href="https://github.com/zongzibinbin">abin</a>
 * @since 2023-03-25
 */
@Service
public class RoomDao extends ServiceImpl<RoomMapper, Room> {
    @Autowired
    private CursorUtils cursorUtils;

    public CursorPageBaseResp<Room> getCursorPage(CursorPageBaseReq request) {
        return cursorUtils.getCursorPageByMysql(this, request, wrapper -> {
            wrapper.ne(Room::getType, RoomTypeEnum.GROUP.getStatus());
        }, Room::getActiveTime);
    }
}
