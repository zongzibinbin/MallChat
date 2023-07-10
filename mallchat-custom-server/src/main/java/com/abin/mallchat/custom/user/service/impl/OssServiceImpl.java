package com.abin.mallchat.custom.user.service.impl;

import com.abin.mallchat.common.common.utils.AssertUtil;
import com.abin.mallchat.common.common.utils.oss.MinIOTemplate;
import com.abin.mallchat.common.common.utils.oss.domain.OssReq;
import com.abin.mallchat.common.common.utils.oss.domain.OssResp;
import com.abin.mallchat.custom.user.domain.enums.OssSceneEnum;
import com.abin.mallchat.custom.user.domain.vo.request.oss.UploadUrlReq;
import com.abin.mallchat.custom.user.service.OssService;
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
