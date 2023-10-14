package com.abin.mallchat.common.chat.domain.entity.msg;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * Description: 视频消息入参
 * Author: <a href="https://github.com/zongzibinbin">abin</a>
 * Date: 2023-06-04
 */
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class VideoMsgDTO extends BaseFileDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("缩略图宽度（像素）")
    @NotNull
    private Integer thumbWidth;

    @ApiModelProperty("缩略图高度（像素）")
    @NotNull
    private Integer thumbHeight;

    @ApiModelProperty("缩略图大小（字节）")
    @NotNull
    private Long thumbSize;

    @ApiModelProperty("缩略图下载地址")
    @NotBlank
    private String thumbUrl;

}
