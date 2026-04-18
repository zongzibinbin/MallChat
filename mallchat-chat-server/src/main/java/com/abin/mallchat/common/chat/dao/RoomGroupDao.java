package com.abin.mallchat.common.chat.dao;

import com.abin.mallchat.common.chat.domain.entity.RoomGroup;
import com.abin.mallchat.common.chat.mapper.RoomGroupMapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 群聊房间表 服务实现类
 * </p>
 *
 * @author <a href="https://github.com/zongzibinbin">abin</a>
 * @since 2023-07-22
 */
@Service
public class RoomGroupDao extends ServiceImpl<RoomGroupMapper, RoomGroup> {

    public List<RoomGroup> listByRoomIds(List<Long> roomIds) {
        return lambdaQuery()
                .in(RoomGroup::getRoomId, roomIds)
                .list();
    }

    public RoomGroup getByRoomId(Long roomId) {
        return lambdaQuery()
                .eq(RoomGroup::getRoomId, roomId)
                .one();
    }

    /**
     * 更新全员禁言状态
     *
     * @param roomId    房间ID
     * @param isAllMute 是否全员禁言
     */
    public void updateAllMute(Long roomId, boolean isAllMute) {
        LambdaUpdateWrapper<RoomGroup> wrapper = new UpdateWrapper<RoomGroup>().lambda()
                .eq(RoomGroup::getRoomId, roomId)
                .set(RoomGroup::getAllMute, isAllMute ? 1 : 0);
        this.update(wrapper);
    }
}
