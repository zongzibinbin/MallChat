package com.abin.mallchat.common.chat.dao;

import com.abin.mallchat.common.chat.domain.entity.GroupAnnouncement;
import com.abin.mallchat.common.chat.mapper.GroupAnnouncementMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GroupAnnouncementDao extends ServiceImpl<GroupAnnouncementMapper, GroupAnnouncement> {

    public GroupAnnouncement getLatestAnnouncement(Long roomId) {
        return lambdaQuery()
                .eq(GroupAnnouncement::getRoomId, roomId)
                .orderByDesc(GroupAnnouncement::getCreateTime)
                .last("LIMIT 1")
                .one();
    }

    public List<GroupAnnouncement> getAnnouncementList(Long roomId, int pageSize) {
        return lambdaQuery()
                .eq(GroupAnnouncement::getRoomId, roomId)
                .orderByDesc(GroupAnnouncement::getCreateTime)
                .last("LIMIT " + pageSize)
                .list();
    }

    public Page<GroupAnnouncement> getAnnouncementPage(Long roomId, Long cursor, int pageSize) {
        Page<GroupAnnouncement> page = new Page<>(1, pageSize);
        if (cursor != null) {
            return lambdaQuery()
                    .eq(GroupAnnouncement::getRoomId, roomId)
                    .lt(GroupAnnouncement::getId, cursor)
                    .orderByDesc(GroupAnnouncement::getCreateTime)
                    .page(page);
        }
        return lambdaQuery()
                .eq(GroupAnnouncement::getRoomId, roomId)
                .orderByDesc(GroupAnnouncement::getCreateTime)
                .page(page);
    }
}
