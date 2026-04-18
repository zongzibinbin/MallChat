package com.abin.mallchat.common.user.domain.vo.response.auth;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Description: 用户名密码登录响应
 * Author: <a href="https://github.com/zongzibinbin">abin</a>
 * Date: 2026-04-18
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("用户名密码登录响应")
public class LoginResp {

    @ApiModelProperty("用户id")
    private Long uid;

    @ApiModelProperty("用户头像")
    private String avatar;

    @ApiModelProperty("登录token")
    private String token;

    @ApiModelProperty("用户昵称")
    private String name;

    @ApiModelProperty("用户权限 0普通用户 1超管")
    private Integer power;
}
