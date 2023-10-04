package com.abin.mallchat.oss.domain;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * Description: 上传url请求出参
 * Author: <a href="https://github.com/zongzibinbin">abin</a>
 * Date: 2023-03-23
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OssResp {

    @ApiModelProperty(value = "上传的临时url")
    private String uploadUrl;

    @ApiModelProperty(value = "成功后能够下载的url")
    private String downloadUrl;
}
