package com.abin.mallchat.custom.user.controller;


import com.abin.mallchat.common.common.domain.vo.request.CursorPageBaseReq;
import com.abin.mallchat.common.common.domain.vo.request.PageBaseReq;
import com.abin.mallchat.common.common.domain.vo.response.ApiResult;
import com.abin.mallchat.common.common.domain.vo.response.CursorPageBaseResp;
import com.abin.mallchat.common.common.domain.vo.response.PageBaseResp;
import com.abin.mallchat.common.common.utils.RequestHolder;
import com.abin.mallchat.common.user.service.cache.UserCache;
import com.abin.mallchat.custom.chat.domain.vo.response.ChatMemberResp;
import com.abin.mallchat.custom.chat.service.ChatService;
import com.abin.mallchat.custom.user.domain.vo.request.friend.FriendApplyReq;
import com.abin.mallchat.custom.user.domain.vo.request.friend.FriendApproveReq;
import com.abin.mallchat.custom.user.domain.vo.request.friend.FriendCheckReq;
import com.abin.mallchat.custom.user.domain.vo.request.friend.FriendDeleteReq;
import com.abin.mallchat.custom.user.domain.vo.response.friend.FriendApplyResp;
import com.abin.mallchat.custom.user.domain.vo.response.friend.FriendCheckResp;
import com.abin.mallchat.custom.user.domain.vo.response.friend.FriendUnreadResp;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * <p>
 * 好友相关接口
 * </p>
 *
 * @author <a href="https://github.com/zongzibinbin">abin</a>
 * @since 2023-07-16
 */
@RestController
@RequestMapping("/capi/user/friend")
@Api(tags = "好友相关接口")
@Slf4j
public class FriendController {
    @Autowired
    private ChatService chatService;
    @Autowired
    private UserCache userCache;

    @GetMapping("/check")
    @ApiOperation("批量判断是否是自己好友")
    public ApiResult<FriendCheckResp> check(@Valid FriendCheckReq request) {//todo
        return ApiResult.success();
    }

    @PostMapping("/apply")
    @ApiOperation("申请好友")
    public ApiResult<Void> apply(@Valid @RequestBody FriendApplyReq request) {//todo
        Long uid = RequestHolder.get().getUid();
        return ApiResult.success();
    }

    @DeleteMapping()
    @ApiOperation("删除好友")
    public ApiResult<Void> delete(@Valid @RequestBody FriendDeleteReq request) {//todo
        Long uid = RequestHolder.get().getUid();
        return ApiResult.success();
    }

    @GetMapping("/apply/page")
    @ApiOperation("好友申请列表")
    public ApiResult<PageBaseResp<FriendApplyResp>> page(@Valid PageBaseReq request) {//todo
        Long uid = RequestHolder.get().getUid();
        return ApiResult.success();
    }

    @GetMapping("/apply/unread")
    @ApiOperation("申请未读数")
    public ApiResult<FriendUnreadResp> unread() {//todo
        Long uid = RequestHolder.get().getUid();
        return ApiResult.success();
    }

    @PutMapping("/apply")
    @ApiOperation("申请审批")
    public ApiResult<Void> applyApprove(@Valid @RequestBody FriendApproveReq request) {//todo
        Long uid = RequestHolder.get().getUid();
        return ApiResult.success();
    }

    @PutMapping("/page")
    @ApiOperation("联系人列表")
    public ApiResult<CursorPageBaseResp<ChatMemberResp>> applyApprove(@Valid CursorPageBaseReq request) {//todo
        Long uid = RequestHolder.get().getUid();
        return ApiResult.success();
    }
}

