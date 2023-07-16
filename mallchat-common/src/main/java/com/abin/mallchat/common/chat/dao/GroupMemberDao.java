package com.abin.mallchat.common.chat.dao;

import com.abin.mallchat.common.chat.domain.entity.GroupMember;
import com.abin.mallchat.common.chat.mapper.GroupMemberMapper;
import com.abin.mallchat.common.chat.service.IGroupMemberService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 群成员表 服务实现类
 * </p>
 *
 * @author <a href="https://github.com/zongzibinbin">abin</a>
 * @since 2023-07-16
 */
@Service
public class GroupMemberDao extends ServiceImpl<GroupMemberMapper, GroupMember> implements IGroupMemberService {

}
