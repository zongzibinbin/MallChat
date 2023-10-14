package com.abin.mallchat.common.user.service.impl;

import com.abin.mallchat.common.common.utils.AssertUtil;
import com.abin.mallchat.common.user.domain.enums.OssSceneEnum;
import com.abin.mallchat.common.user.domain.vo.request.oss.UploadUrlReq;
import com.abin.mallchat.common.user.service.OssService;
import com.abin.mallchat.oss.MinIOTemplate;
import com.abin.mallchat.oss.domain.OssReq;
import com.abin.mallchat.oss.domain.OssResp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Description:
 * Author: <a href="https://github.com/zongzibinbin">abin</a>
 * Date: 2023-06-20
 */
@Service
public class OssServiceImpl implements OssService {
    @Autowired
    private MinIOTemplate minIOTemplate;

    @Override
    public OssResp getUploadUrl(Long uid, UploadUrlReq req) {
        OssSceneEnum sceneEnum = OssSceneEnum.of(req.getScene());
        AssertUtil.isNotEmpty(sceneEnum, "场景有误");
        OssReq ossReq = OssReq.builder()
                .fileName(req.getFileName())
                .filePath(sceneEnum.getPath())
                .uid(uid)
                .build();
        return minIOTemplate.getPreSignedObjectUrl(ossReq);
    }
}
