package com.abin.mallchat.common.user.domain.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * Description: 修改用户名
 * Author: <a href="https://github.com/zongzibinbin">abin</a>
 * Date: 2023-03-23
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ItemInfoDTO {
    @ApiModelProperty(value = "徽章id")
    private Long itemId;
    @ApiModelProperty("徽章图像")
    private String img;
    @ApiModelProperty("徽章说明")
    private String describe;

}
