package com.abin.mallchat.common.user.domain.vo.response.ws;

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
public class WSAnnouncement {

    @ApiModelProperty("群组id")
    private Long roomId;

    @ApiModelProperty("公告id")
    private Long announcementId;

    @ApiModelProperty("公告内容")
    private String content;

    @ApiModelProperty("发布者uid")
    private Long publisherUid;

    @ApiModelProperty("发布时间")
    private Date createTime;
}
