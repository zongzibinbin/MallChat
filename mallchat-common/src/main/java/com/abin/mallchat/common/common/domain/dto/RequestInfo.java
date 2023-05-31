package com.abin.mallchat.common.common.domain.dto;

import lombok.Data;

/**
 * <p>
 * web请求信息收集类
 * </p>
 *
 * @author <a href="https://github.com/zongzibinbin">abin</a>
 * @since 2023-04-05
 */
@Data
public class RequestInfo {

    private Long uid;

    private String ip;

}
