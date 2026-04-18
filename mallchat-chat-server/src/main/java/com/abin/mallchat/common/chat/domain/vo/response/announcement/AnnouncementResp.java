package com.abin.mallchat.common.chat.domain.vo.response.announcement;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AnnouncementResp {

    @ApiModelProperty("公告id")
    private Long id;

    @ApiModelProperty("房间id")
    private Long roomId;

    @ApiModelProperty("公告内容")
    private String content;

    @ApiModelProperty("发布者uid")
    private Long publisherUid;

    @ApiModelProperty("发布者名称")
    private String publisherName;

    @ApiModelProperty("发布者头像")
    private String publisherAvatar;

    @ApiModelProperty("发布时间")
    private Date createTime;
}
