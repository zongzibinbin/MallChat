package com.abin.mallchat.common.chat.domain.vo.request.member;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * Description: 成员禁言请求
 * Author: abin
 * Date: 2024-04-18
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberMuteReq {
    @NotNull
    @ApiModelProperty("房间号")
    private Long roomId;

    @NotNull
    @ApiModelProperty("需要禁言的用户id")
    private Long uid;

    @ApiModelProperty("禁言时长（分钟），传0或null表示解除禁言")
    private Integer muteTime;
}
