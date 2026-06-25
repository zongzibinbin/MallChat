package com.abin.mallchat.common.common.dao;

import com.abin.mallchat.common.common.domain.entity.WebLog;
import com.abin.mallchat.common.common.mapper.WebLogMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 接口埋点日志
 */
@Service
public class WebLogDao extends ServiceImpl<WebLogMapper, WebLog> {
}
