package com.abin.mallchat.common.user.domain.vo.request.user;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Size;
import java.util.List;


/**
 * Description: 批量查询用户汇总详情
 * Author: <a href="https://github.com/zongzibinbin">abin</a>
 * Date: 2023-03-23
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SummeryInfoReq {
    @ApiModelProperty(value = "用户信息入参")
    @Size(max = 50)
    private List<infoReq> reqList;

    @Data
    public static class infoReq {
        @ApiModelProperty(value = "uid")
        private Long uid;
        @ApiModelProperty(value = "最近一次更新用户信息时间")
        private Long lastModifyTime;
    }
}
