package com.abin.mallchat.common.chat.domain.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Description: 房间详情
 * Author: <a href="https://github.com/zongzibinbin">abin</a>
 * Date: 2023-07-22
 */
@Data
public class RoomBaseInfo {
    @ApiModelProperty("房间id")
    private Long roomId;
    @ApiModelProperty("会话名称")
    private String name;
    @ApiModelProperty("会话头像")
    private String avatar;
}
