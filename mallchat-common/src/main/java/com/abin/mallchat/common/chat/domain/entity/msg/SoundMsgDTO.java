package com.abin.mallchat.common.chat.domain.entity.msg;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Description: 语音消息入参
 * Author: <a href="https://github.com/zongzibinbin">abin</a>
 * Date: 2023-06-04
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SoundMsgDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty("大小（字节）")
    private Long size;

    @ApiModelProperty("时长（秒）")
    private Integer second;

    @ApiModelProperty("下载地址")
    private String url;
}
