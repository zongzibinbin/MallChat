package com.abin.mallchat.common.user.dao;

import com.abin.mallchat.common.user.domain.entity.UserFriend;
import com.abin.mallchat.common.user.mapper.UserFriendMapper;
import com.abin.mallchat.common.user.service.IUserFriendService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户联系人表 服务实现类
 * </p>
 *
 * @author <a href="https://github.com/zongzibinbin">abin</a>
 * @since 2023-07-16
 */
@Service
public class UserFriendDao extends ServiceImpl<UserFriendMapper, UserFriend> implements IUserFriendService {

}
