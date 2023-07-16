package com.abin.mallchat.common.user.dao;

import com.abin.mallchat.common.user.domain.entity.UserApply;
import com.abin.mallchat.common.user.mapper.UserApplyMapper;
import com.abin.mallchat.common.user.service.IUserApplyService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

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

}
