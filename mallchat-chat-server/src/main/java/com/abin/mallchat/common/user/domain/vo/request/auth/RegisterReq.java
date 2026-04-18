package com.abin.mallchat.common.user.domain.vo.request.auth;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

/**
 * Description: 用户名密码注册请求
 * Author: <a href="https://github.com/zongzibinbin">abin</a>
 * Date: 2026-04-18
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("用户名密码注册请求")
public class RegisterReq {

    @NotBlank(message = "账号不能为空")
    @Length(min = 4, max = 32, message = "账号长度需要在4-32位之间")
    @Pattern(regexp = "^[A-Za-z0-9_]+$", message = "账号仅支持字母、数字和下划线")
    @ApiModelProperty("登录账号")
    private String username;

    @NotBlank(message = "密码不能为空")
    @Length(min = 6, max = 20, message = "密码长度需要在6-20位之间")
    @ApiModelProperty("登录密码")
    private String password;

    @Length(max = 6, message = "昵称可别取太长，不然我记不住噢")
    @ApiModelProperty("用户昵称")
    private String nickname;
}
