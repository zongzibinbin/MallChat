package com.abin.mallchat.common.chat.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.abin.mallchat.common.chat.dao.GroupAnnouncementDao;
import com.abin.mallchat.common.chat.dao.GroupMemberDao;
import com.abin.mallchat.common.chat.dao.RoomGroupDao;
import com.abin.mallchat.common.chat.domain.entity.GroupAnnouncement;
import com.abin.mallchat.common.chat.domain.entity.RoomGroup;
import com.abin.mallchat.common.chat.domain.enums.GroupRoleEnum;
import com.abin.mallchat.common.chat.domain.vo.request.announcement.AnnouncementAddReq;
import com.abin.mallchat.common.chat.domain.vo.response.announcement.AnnouncementResp;
import com.abin.mallchat.common.chat.service.IGroupAnnouncementService;
import com.abin.mallchat.common.chat.service.adapter.AnnouncementAdapter;
import com.abin.mallchat.common.chat.service.cache.GroupMemberCache;
import com.abin.mallchat.common.common.exception.GroupErrorEnum;
import com.abin.mallchat.common.common.utils.AssertUtil;
import com.abin.mallchat.common.user.domain.entity.User;
import com.abin.mallchat.common.user.domain.enums.WSBaseResp;
import com.abin.mallchat.common.user.service.cache.UserCache;
import com.abin.mallchat.common.user.service.impl.PushService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class GroupAnnouncementServiceImpl implements IGroupAnnouncementService {

    @Autowired
    private GroupAnnouncementDao groupAnnouncementDao;

    @Autowired
    private RoomGroupDao roomGroupDao;

    @Autowired
    private GroupMemberDao groupMemberDao;

    @Autowired
    private GroupMemberCache groupMemberCache;

    @Autowired
    private UserCache userCache;

    @Autowired
    private PushService pushService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long addAnnouncement(Long uid, AnnouncementAddReq request) {
        Long roomId = request.getRoomId();
        
        RoomGroup roomGroup = roomGroupDao.getByRoomId(roomId);
        AssertUtil.isNotEmpty(roomGroup, GroupErrorEnum.GROUP_NOT_EXIST);

        Boolean isLord = groupMemberDao.isLord(roomGroup.getId(), uid);
        Boolean isManager = groupMemberDao.isManager(roomGroup.getId(), uid);
        AssertUtil.isTrue(isLord || isManager, GroupErrorEnum.NOT_ALLOWED_OPERATION);

        GroupAnnouncement announcement = GroupAnnouncement.builder()
                .roomId(roomId)
                .content(request.getContent())
                .publisherUid(uid)
                .build();
        groupAnnouncementDao.save(announcement);

        List<Long> memberUidList = groupMemberCache.getMemberUidList(roomId);
        if (CollectionUtil.isNotEmpty(memberUidList)) {
            WSBaseResp<?> ws = AnnouncementAdapter.buildAnnouncementWS(roomId, announcement);
            pushService.sendPushMsg(ws, memberUidList);
        }

        return announcement.getId();
    }

    @Override
    public AnnouncementResp getLatestAnnouncement(Long uid, Long roomId) {
        RoomGroup roomGroup = roomGroupDao.getByRoomId(roomId);
        AssertUtil.isNotEmpty(roomGroup, GroupErrorEnum.GROUP_NOT_EXIST);

        Boolean isGroupShip = groupMemberDao.isGroupShip(roomId, Collections.singletonList(uid));
        AssertUtil.isTrue(isGroupShip, GroupErrorEnum.USER_NOT_IN_GROUP);

        GroupAnnouncement announcement = groupAnnouncementDao.getLatestAnnouncement(roomId);
        if (Objects.isNull(announcement)) {
            return null;
        }

        User publisher = userCache.getUserInfo(announcement.getPublisherUid());
        return AnnouncementAdapter.buildAnnouncementResp(announcement, publisher);
    }

    @Override
    public List<AnnouncementResp> getAnnouncementList(Long uid, Long roomId, int pageSize) {
        RoomGroup roomGroup = roomGroupDao.getByRoomId(roomId);
        AssertUtil.isNotEmpty(roomGroup, GroupErrorEnum.GROUP_NOT_EXIST);

        Boolean isGroupShip = groupMemberDao.isGroupShip(roomId, Collections.singletonList(uid));
        AssertUtil.isTrue(isGroupShip, GroupErrorEnum.USER_NOT_IN_GROUP);

        List<GroupAnnouncement> announcementList = groupAnnouncementDao.getAnnouncementList(roomId, pageSize);
        if (CollectionUtil.isEmpty(announcementList)) {
            return Collections.emptyList();
        }

        List<Long> publisherUids = announcementList.stream()
                .map(GroupAnnouncement::getPublisherUid)
                .distinct()
                .collect(Collectors.toList());
        Map<Long, User> userMap = userCache.getUserInfoBatch(publisherUids.stream().collect(Collectors.toSet()));

        return announcementList.stream()
                .map(a -> AnnouncementAdapter.buildAnnouncementResp(a, userMap.get(a.getPublisherUid())))
                .collect(Collectors.toList());
    }
}
