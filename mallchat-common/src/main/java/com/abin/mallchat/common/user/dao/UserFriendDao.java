package com.abin.mallchat.common.user.dao;

import com.abin.mallchat.common.user.domain.entity.UserFriend;
import com.abin.mallchat.common.user.mapper.UserFriendMapper;
import com.abin.mallchat.common.user.service.IUserFriendService;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public List<UserFriend> queryUserFriend(Long uid, List<Long> friendUidList) {
        LambdaQueryChainWrapper<UserFriend> wrapper = lambdaQuery()
                .eq(UserFriend::getUid, uid).in(UserFriend::getFriendUid, friendUidList);
        return list(wrapper);
    }

    public void insertBatch(List<UserFriend> userFriends) {
        saveBatch(userFriends);
    }
}
