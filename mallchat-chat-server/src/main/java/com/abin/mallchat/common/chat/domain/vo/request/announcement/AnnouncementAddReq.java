package com.abin.mallchat.common.chat.domain.vo.request.announcement;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class AnnouncementAddReq {

    @NotNull
    @ApiModelProperty("房间id")
    private Long roomId;

    @NotBlank
    @Size(max = 1000, message = "公告内容不能超过1000字")
    @ApiModelProperty("公告内容")
    private String content;
}
