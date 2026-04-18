package com.abin.mallchat.common.chat.controller;


import com.abin.mallchat.common.chat.domain.dto.MsgReadInfoDTO;
import com.abin.mallchat.common.chat.domain.vo.request.*;
import com.abin.mallchat.common.chat.domain.vo.response.ChatMessageReadResp;
import com.abin.mallchat.common.chat.domain.vo.response.ChatMessageResp;
import com.abin.mallchat.common.chat.service.ChatService;
import com.abin.mallchat.common.common.annotation.FrequencyControl;
import com.abin.mallchat.common.common.domain.vo.response.ApiResult;
import com.abin.mallchat.common.common.domain.vo.response.CursorPageBaseResp;
import com.abin.mallchat.common.common.utils.RequestHolder;
import com.abin.mallchat.common.user.domain.enums.BlackTypeEnum;
import com.abin.mallchat.common.user.service.cache.UserCache;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * <p>
 * 群聊相关接口
 * </p>
 *
 * @author <a href="https://github.com/zongzibinbin">abin</a>
 * @since 2023-03-19
 */
@RestController
@RequestMapping("/capi/chat")
@Api(tags = "聊天室相关接口")
@Slf4j
public class ChatController {
    @Autowired
    private ChatService chatService;
    @Autowired
    private UserCache userCache;

    private Set<String> getBlackUidSet() {
        return userCache.getBlackMap().getOrDefault(BlackTypeEnum.UID.getType(), new HashSet<>());
    }

    @GetMapping("/public/msg/page")
    @ApiOperation("获取消息列表（游标分页）")
//    @FrequencyControl(time = 120, count = 20, target = FrequencyControl.Target.IP)
    public ApiResult<CursorPageBaseResp<ChatMessageResp>> getMsgPage(
            @ApiParam(value = "消息列表查询请求参数", required = true)
            @Valid ChatMessagePageReq request) {
        CursorPageBaseResp<ChatMessageResp> msgPage = chatService.getMsgPage(request, RequestHolder.get().getUid());
        filterBlackMsg(msgPage);
        return ApiResult.success(msgPage);
    }

    private void filterBlackMsg(CursorPageBaseResp<ChatMessageResp> memberPage) {
        Set<String> blackMembers = getBlackUidSet();
        memberPage.getList().removeIf(a -> blackMembers.contains(a.getFromUser().getUid().toString()));
    }

    @PostMapping("/msg")
    @ApiOperation("发送消息")
    @FrequencyControl(time = 5, count = 3, target = FrequencyControl.Target.UID)
    @FrequencyControl(time = 30, count = 5, target = FrequencyControl.Target.UID)
    @FrequencyControl(time = 60, count = 10, target = FrequencyControl.Target.UID)
    public ApiResult<ChatMessageResp> sendMsg(
            @ApiParam(value = "发送消息请求参数，包含房间ID、消息类型和消息内容", required = true)
            @Valid @RequestBody ChatMessageReq request) {
        Long msgId = chatService.sendMsg(request, RequestHolder.get().getUid());
        //返回完整消息格式，方便前端展示
        return ApiResult.success(chatService.getMsgResp(msgId, RequestHolder.get().getUid()));
    }

    @PutMapping("/msg/mark")
    @ApiOperation("消息标记（点赞/举报）")
    @FrequencyControl(time = 10, count = 5, target = FrequencyControl.Target.UID)
    public ApiResult<Void> setMsgMark(
            @ApiParam(value = "消息标记请求参数，markType:1-点赞,2-举报；actType:1-确认,2-取消", required = true)
            @Valid @RequestBody ChatMessageMarkReq request) {
        chatService.setMsgMark(RequestHolder.get().getUid(), request);
        return ApiResult.success();
    }

    @PutMapping("/msg/recall")
    @ApiOperation("撤回消息")
    @FrequencyControl(time = 20, count = 3, target = FrequencyControl.Target.UID)
    public ApiResult<Void> recallMsg(
            @ApiParam(value = "撤回消息请求参数，包含消息ID和会话ID", required = true)
            @Valid @RequestBody ChatMessageBaseReq request) {
        chatService.recallMsg(RequestHolder.get().getUid(), request);
        return ApiResult.success();
    }

    @GetMapping("/msg/read/page")
    @ApiOperation("获取消息已读/未读用户列表（游标分页）")
    public ApiResult<CursorPageBaseResp<ChatMessageReadResp>> getReadPage(
            @ApiParam(value = "消息已读/未读列表查询参数，searchType:1-已读,2-未读", required = true)
            @Valid ChatMessageReadReq request) {
        Long uid = RequestHolder.get().getUid();
        return ApiResult.success(chatService.getReadPage(uid, request));
    }

    @GetMapping("/msg/read")
    @ApiOperation("批量获取消息已读未读总数")
    public ApiResult<Collection<MsgReadInfoDTO>> getReadInfo(
            @ApiParam(value = "消息已读未读信息查询参数，msgIds最大长度20，只查本人")
            @Valid ChatMessageReadInfoReq request) {
        Long uid = RequestHolder.get().getUid();
        return ApiResult.success(chatService.getMsgReadInfo(uid, request));
    }

    @PutMapping("/msg/read")
    @ApiOperation("消息阅读上报（标记会话为已读）")
    public ApiResult<Void> msgRead(
            @ApiParam(value = "消息阅读上报参数，包含会话ID", required = true)
            @Valid @RequestBody ChatMessageMemberReq request) {
        Long uid = RequestHolder.get().getUid();
        chatService.msgRead(uid, request);
        return ApiResult.success();
    }
}

