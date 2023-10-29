package com.abin.mallchat.common.chat.service.impl;

import com.abin.mallchat.common.chat.dao.GroupMemberDao;
import com.abin.mallchat.common.chat.dao.RoomGroupDao;
import com.abin.mallchat.common.chat.domain.entity.RoomGroup;
import com.abin.mallchat.common.chat.domain.vo.request.AdminAddReq;
import com.abin.mallchat.common.chat.service.IGroupMemberService;
import com.abin.mallchat.common.common.exception.GroupErrorEnum;
import com.abin.mallchat.common.common.utils.AssertUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

import static com.abin.mallchat.common.chat.constant.GroupConst.MAX_MANAGE_COUNT;

/**
 * @Author Kkuil
 * @Date 2023/10/24 15:45
 * @Description 群成员服务类
 */
@Service
public class GroupMemberServiceImpl implements IGroupMemberService {

    @Autowired
    private GroupMemberDao groupMemberDao;

    @Autowired
    private RoomGroupDao roomGroupDao;

    /**
     * 增加管理员
     *
     * @param uid     用户ID
     * @param request 请求信息
     */
    @Override
    public void addAdmin(Long uid, AdminAddReq request) {
        // 1. 判断群聊是否存在
        RoomGroup roomGroup = roomGroupDao.getByRoomId(request.getRoomId());
        AssertUtil.isNotEmpty(roomGroup, GroupErrorEnum.GROUP_NOT_EXIST);

        // 2. 判断该用户是否是群主
        Boolean isLord = groupMemberDao.isLord(roomGroup.getId(), uid);
        AssertUtil.isTrue(isLord, GroupErrorEnum.NOT_ALLOWED_OPERATION);

        // 3. 判断群成员是否在群中
        Boolean isGroupShip = groupMemberDao.isGroupShip(roomGroup.getRoomId(), request.getUidList());
        AssertUtil.isTrue(isGroupShip, GroupErrorEnum.USER_NOT_IN_GROUP);

        // 4. 判断管理员数量是否达到上限
        // 4.1 查询现有管理员数量
        List<Long> manageUidList = groupMemberDao.getManageUidList(roomGroup.getId());
        // 4.2 去重
        HashSet<Long> manageUidSet = new HashSet<>(manageUidList);
        manageUidSet.addAll(request.getUidList());
        AssertUtil.isFalse(manageUidSet.size() > MAX_MANAGE_COUNT, GroupErrorEnum.MANAGE_COUNT_EXCEED);

        // 5. 增加管理员
        groupMemberDao.addAdmin(roomGroup.getId(), request.getUidList());
    }
}
