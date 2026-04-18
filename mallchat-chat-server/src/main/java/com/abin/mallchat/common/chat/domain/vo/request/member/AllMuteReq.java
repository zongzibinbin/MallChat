package com.abin.mallchat.common.chat.domain.vo.request.member;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * Description: 全员禁言请求
 * Author: abin
 * Date: 2024-04-18
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AllMuteReq {
    @NotNull
    @ApiModelProperty("房间号")
    private Long roomId;

    @NotNull
    @ApiModelProperty("是否开启全员禁言，true-开启，false-关闭")
    private Boolean isAllMute;
}
