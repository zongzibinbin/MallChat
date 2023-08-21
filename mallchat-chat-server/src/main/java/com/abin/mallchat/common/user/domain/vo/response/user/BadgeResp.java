package com.abin.mallchat.common.user.domain.vo.response.user;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Description: 徽章信息
 * Author: <a href="https://github.com/zongzibinbin">abin</a>
 * Date: 2023-03-22
 */
@Data
@ApiModel("徽章信息")
public class BadgeResp {

    @ApiModelProperty(value = "徽章id")
    private Long id;

    @ApiModelProperty(value = "徽章图标")
    private String img;

    @ApiModelProperty(value = "徽章描述")
    private String describe;

    @ApiModelProperty(value = "是否拥有 0否 1是")
    private Integer obtain;

    @ApiModelProperty(value = "是否佩戴  0否 1是")
    private Integer wearing;
}
