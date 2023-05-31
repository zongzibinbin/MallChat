package com.abin.mallchat.common.user.service;

/**
 * <p>
 * IP 服务类
 * </p>
 *
 * @author <a href="https://github.com/zongzibinbin">abin</a>
 * @since 2023-05-21
 */
public interface IpService {
    /**
     * 异步更新用户ip详情
     *
     * @param uid 用户id
     */
    void refreshIpDetailAsync(Long uid);

}
