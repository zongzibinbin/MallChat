package com.abin.mallchat.common.chat.service.adapter;

import com.abin.mallchat.common.chat.domain.entity.GroupAnnouncement;
import com.abin.mallchat.common.chat.domain.vo.response.announcement.AnnouncementResp;
import com.abin.mallchat.common.user.domain.entity.User;
import com.abin.mallchat.common.user.domain.enums.WSBaseResp;
import com.abin.mallchat.common.user.domain.enums.WSRespTypeEnum;
import com.abin.mallchat.common.user.domain.vo.response.ws.WSAnnouncement;

import java.util.Objects;

public class AnnouncementAdapter {

    public static WSBaseResp<WSAnnouncement> buildAnnouncementWS(Long roomId, GroupAnnouncement announcement) {
        WSBaseResp<WSAnnouncement> wsBaseResp = new WSBaseResp<>();
        wsBaseResp.setType(WSRespTypeEnum.ANNOUNCEMENT.getType());
        
        WSAnnouncement wsAnnouncement = new WSAnnouncement();
        wsAnnouncement.setRoomId(roomId);
        wsAnnouncement.setAnnouncementId(announcement.getId());
        wsAnnouncement.setContent(announcement.getContent());
        wsAnnouncement.setPublisherUid(announcement.getPublisherUid());
        wsAnnouncement.setCreateTime(announcement.getCreateTime());
        
        wsBaseResp.setData(wsAnnouncement);
        return wsBaseResp;
    }

    public static AnnouncementResp buildAnnouncementResp(GroupAnnouncement announcement, User publisher) {
        AnnouncementResp resp = new AnnouncementResp();
        resp.setId(announcement.getId());
        resp.setRoomId(announcement.getRoomId());
        resp.setContent(announcement.getContent());
        resp.setPublisherUid(announcement.getPublisherUid());
        resp.setCreateTime(announcement.getCreateTime());
        
        if (Objects.nonNull(publisher)) {
            resp.setPublisherName(publisher.getName());
            resp.setPublisherAvatar(publisher.getAvatar());
        }
        return resp;
    }
}
