package com.abin.mallchat.common.user.dao;

import com.abin.mallchat.common.user.domain.entity.UserApply;
import com.abin.mallchat.common.user.mapper.UserApplyMapper;
import com.abin.mallchat.common.user.service.IUserApplyService;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.abin.mallchat.common.user.domain.enums.ApplyReadStatusEnum.UNREAD;
import static com.abin.mallchat.common.user.domain.enums.ApplyStatusEnum.AGREE;

/**
 * <p>
 * 用户申请表 服务实现类
 * </p>
 *
 * @author <a href="https://github.com/zongzibinbin">abin</a>
 * @since 2023-07-16
 */
@Service
public class UserApplyDao extends ServiceImpl<UserApplyMapper, UserApply> implements IUserApplyService {

    public UserApply queryUserApply(Long uid, Long targetUid) {
        LambdaQueryChainWrapper<UserApply> wrapper = lambdaQuery()
                .eq(UserApply::getUid, uid)
                .eq(UserApply::getTargetId, targetUid);
        return getOne(wrapper);
    }

    public void insert(UserApply userApply) {
        save(userApply);
    }

    public List<UserApply> queryUserApplyList(Long uid) {
        LambdaQueryChainWrapper<UserApply> wrapper = lambdaQuery()
                .eq(UserApply::getUid, uid)
                .or()
                .eq(UserApply::getTargetId, uid);
        return list(wrapper);
    }

    public Integer unreadCount(Long uid) {
        LambdaQueryChainWrapper<UserApply> wrapper = lambdaQuery()
                .eq(UserApply::getTargetId, uid)
                .eq(UserApply::getReadStatus, UNREAD.getCode());
        return count(wrapper);
    }

    public UserApply queryUserApplyById(Long applyId) {
        return getById(applyId);
    }

    public void agreeUserApply(Long applyId) {
        LambdaUpdateChainWrapper<UserApply> updateWrapper = lambdaUpdate()
                .set(UserApply::getStatus, AGREE.getCode())
                .eq(UserApply::getId, applyId);
        update(updateWrapper);
    }
}
