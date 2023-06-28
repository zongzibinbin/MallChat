package com.abin.mallchat.custom.user.service;

import com.abin.mallchat.common.common.utils.oss.domain.OssResp;
import com.abin.mallchat.custom.user.domain.vo.request.oss.UploadUrlReq;

/**
 * <p>
 * oss 服务类
 * </p>
 *
 * @author <a href="https://github.com/zongzibinbin">abin</a>
 * @since 2023-03-19
 */
public interface OssService {

    /**
     * 获取临时的上传链接
     */
    OssResp getUploadUrl(Long uid, UploadUrlReq req);
}
