package com.abin.mallchat.oss.domain;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * Description: 上传url请求入参
 * Author: <a href="https://github.com/zongzibinbin">abin</a>
 * Date: 2023-03-23
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OssReq {
    @ApiModelProperty(value = "文件存储路径")
    private String filePath;
    @ApiModelProperty(value = "文件名")
    private String fileName;
    @ApiModelProperty(value = "请求的uid")
    private Long uid;
    @ApiModelProperty(value = "自动生成地址")
    @Builder.Default
    private boolean autoPath = true;
}
