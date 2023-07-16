package com.abin.mallchat.common.chat.dao;

import com.abin.mallchat.common.chat.domain.entity.Contact;
import com.abin.mallchat.common.chat.mapper.ContactMapper;
import com.abin.mallchat.common.chat.service.IContactService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 会话列表 服务实现类
 * </p>
 *
 * @author <a href="https://github.com/zongzibinbin">abin</a>
 * @since 2023-07-16
 */
@Service
public class ContactDao extends ServiceImpl<ContactMapper, Contact> implements IContactService {

}
