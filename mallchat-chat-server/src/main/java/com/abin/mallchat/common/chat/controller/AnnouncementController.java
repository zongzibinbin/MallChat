package com.abin.mallchat.common.chat.controller;

import com.abin.mallchat.common.chat.domain.vo.request.announcement.AnnouncementAddReq;
import com.abin.mallchat.common.chat.domain.vo.response.announcement.AnnouncementResp;
import com.abin.mallchat.common.chat.service.IGroupAnnouncementService;
import com.abin.mallchat.common.common.domain.vo.request.IdReqVO;
import com.abin.mallchat.common.common.domain.vo.response.ApiResult;
import com.abin.mallchat.common.common.domain.vo.response.IdRespVO;
import com.abin.mallchat.common.common.utils.RequestHolder;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/capi/room/announcement")
@Api(tags = "群公告相关接口")
@Slf4j
public class AnnouncementController {

    @Autowired
    private IGroupAnnouncementService groupAnnouncementService;

    @PostMapping
    @ApiOperation("发布群公告")
    public ApiResult<IdRespVO> addAnnouncement(@Valid @RequestBody AnnouncementAddReq request) {
        Long uid = RequestHolder.get().getUid();
        Long announcementId = groupAnnouncementService.addAnnouncement(uid, request);
        return ApiResult.success(IdRespVO.id(announcementId));
    }

    @GetMapping("/latest")
    @ApiOperation("获取群最新公告")
    public ApiResult<AnnouncementResp> getLatestAnnouncement(@Valid IdReqVO request) {
        Long uid = RequestHolder.get().getUid();
        AnnouncementResp announcement = groupAnnouncementService.getLatestAnnouncement(uid, request.getId());
        return ApiResult.success(announcement);
    }

    @GetMapping("/list")
    @ApiOperation("获取群公告列表")
    public ApiResult<List<AnnouncementResp>> getAnnouncementList(@Valid IdReqVO request,
                                                                  @RequestParam(defaultValue = "10") int pageSize) {
        Long uid = RequestHolder.get().getUid();
        List<AnnouncementResp> list = groupAnnouncementService.getAnnouncementList(uid, request.getId(), pageSize);
        return ApiResult.success(list);
    }
}
