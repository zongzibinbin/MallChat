package com.abin.mallchat.common.chat.service;

import com.abin.mallchat.common.chat.domain.vo.request.announcement.AnnouncementAddReq;
import com.abin.mallchat.common.chat.domain.vo.response.announcement.AnnouncementResp;
import com.abin.mallchat.common.common.domain.vo.request.IdReqVO;

import java.util.List;

public interface IGroupAnnouncementService {

    Long addAnnouncement(Long uid, AnnouncementAddReq request);

    AnnouncementResp getLatestAnnouncement(Long uid, Long roomId);

    List<AnnouncementResp> getAnnouncementList(Long uid, Long roomId, int pageSize);
}
