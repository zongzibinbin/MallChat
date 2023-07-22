package com.abin.mallchat.custom.user.service.impl;

import com.abin.mallchat.common.common.domain.vo.request.PageBaseReq;
import com.abin.mallchat.common.common.domain.vo.response.PageBaseResp;
import com.abin.mallchat.common.user.dao.UserApplyDao;
import com.abin.mallchat.common.user.dao.UserFriendDao;
import com.abin.mallchat.common.user.domain.entity.UserApply;
import com.abin.mallchat.common.user.domain.entity.UserFriend;
import com.abin.mallchat.custom.chat.service.adapter.MessageAdapter;
import com.abin.mallchat.custom.user.domain.vo.request.friend.FriendApplyReq;
import com.abin.mallchat.custom.user.domain.vo.request.friend.FriendApproveReq;
import com.abin.mallchat.custom.user.domain.vo.request.friend.FriendCheckReq;
import com.abin.mallchat.custom.user.domain.vo.response.friend.FriendApplyResp;
import com.abin.mallchat.custom.user.domain.vo.response.friend.FriendCheckResp;
import com.abin.mallchat.custom.user.domain.vo.response.friend.FriendUnreadResp;
import com.abin.mallchat.custom.user.domain.vo.response.ws.WSApplyMessage;
import com.abin.mallchat.custom.user.service.FriendService;
import com.abin.mallchat.custom.user.service.WebSocketService;
import com.abin.mallchat.custom.user.service.adapter.WSAdapter;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.abin.mallchat.common.user.domain.enums.ApplyReadStatusEnum.UNREAD;
import static com.abin.mallchat.common.user.domain.enums.ApplyStatusEnum.AGREE;
import static com.abin.mallchat.common.user.domain.enums.ApplyStatusEnum.WAIT_APPROVAL;
import static com.abin.mallchat.common.user.domain.enums.ApplyTypeEnum.ADD_FRIEND;

/**
 * @author : limeng
 * @description : 好友
 * @date : 2023/07/19
 */
@Slf4j
@Service
public class FriendServiceImpl implements FriendService {
    @Resource
    private WebSocketService webSocketService;

    @Resource
    private UserFriendDao userFriendDao;

    @Resource
    private UserApplyDao userApplyDao;

    /**
     * 检查
     * 检查是否是自己好友
     *
     * @param uid     uid
     * @param request 请求
     * @return {@link FriendCheckResp}
     */
    @Override
    public FriendCheckResp check(Long uid, FriendCheckReq request) {
        List<UserFriend> friendList = userFriendDao.queryUserFriend(uid, request.getUidList());
        Map<Long, UserFriend> friendMap = friendList.stream().collect(Collectors.toMap(UserFriend::getFriendUid, friend -> friend));
        List<FriendCheckResp.FriendCheck> friendCheckList = request.getUidList().stream().map(friendUid -> {
            FriendCheckResp.FriendCheck friendCheck = new FriendCheckResp.FriendCheck();
            friendCheck.setUid(friendUid);
            friendCheck.setIsFriend(friendMap.containsKey(friendUid));
            return friendCheck;
        }).collect(Collectors.toList());
        return new FriendCheckResp(friendCheckList);
    }

    /**
     * 申请好友
     *
     * @param request 请求
     */
    @Override
    public void apply(Long uid, FriendApplyReq request) {
        UserApply userApply = userApplyDao.queryUserApply(uid, request.getTargetUid());
        if (Objects.nonNull(userApply)) {
            log.info("已有好友申请记录,uid:{}, targetId:{}", uid, request.getTargetUid());
            return;
        }
        UserApply userApplyNew = new UserApply();
        userApplyNew.setUid(uid);
        userApplyNew.setMsg(request.getMsg());
        userApplyNew.setType(ADD_FRIEND.getCode());
        userApplyNew.setTargetId(request.getTargetUid());
        userApplyNew.setStatus(WAIT_APPROVAL.getCode());
        userApplyNew.setReadStatus(UNREAD.getCode());
        userApplyDao.insert(userApplyNew);

        WSApplyMessage applyMessage = MessageAdapter.buildApplyResp(userApplyNew);
        webSocketService.sendToFriend(WSAdapter.buildApplySend(applyMessage), request.getTargetUid());
    }

    /**
     * 分页查询好友申请
     *
     * @param request 请求
     * @return {@link PageBaseResp}<{@link FriendApplyResp}>
     */
    @Override
    public PageBaseResp<FriendApplyResp> pageApplyFriend(Long uid, PageBaseReq request) {
        // todo 分页
        List<UserApply> userApplyList = userApplyDao.queryUserApplyList(uid);
        List<FriendApplyResp> friendApplyResps = userApplyList.stream().map(userApply -> {
            FriendApplyResp friendApplyResp = new FriendApplyResp();
            friendApplyResp.setApplyId(userApply.getId());
            friendApplyResp.setUid(userApply.getUid());
            friendApplyResp.setType(userApply.getType());
            friendApplyResp.setMsg(userApply.getMsg());
            friendApplyResp.setStatus(userApply.getStatus());
            return friendApplyResp;
        }).collect(Collectors.toList());
        PageBaseResp<FriendApplyResp> pageBaseResp = new PageBaseResp<>();
        pageBaseResp.setList(friendApplyResps);
        return pageBaseResp;
    }

    /**
     * 申请未读数
     *
     * @return {@link FriendUnreadResp}
     */
    @Override
    public FriendUnreadResp unread(Long uid) {
        return new FriendUnreadResp(userApplyDao.unreadCount(uid));
    }

    @Override
    @Transactional
    public void applyApprove(FriendApproveReq request) {
        UserApply userApply = userApplyDao.queryUserApplyById(request.getApplyId());
        if (Objects.isNull(userApply)) {
            log.error("不存在申请记录：{}", request.getApplyId());
            return;
        }
        if (Objects.equals(userApply.getStatus(), AGREE.getCode())) {
            log.error("已同意好友申请：{}", request.getApplyId());
            return;
        }
        userApplyDao.agreeUserApply(request.getApplyId());
        UserFriend userFriend1 = new UserFriend();
        userFriend1.setUid(userApply.getUid());
        userFriend1.setFriendUid(userApply.getTargetId());
        UserFriend userFriend2 = new UserFriend();
        userFriend2.setUid(userApply.getTargetId());
        userFriend2.setFriendUid(userApply.getUid());
        userFriendDao.insertBatch(Lists.newArrayList(userFriend1, userFriend2));
    }
}
