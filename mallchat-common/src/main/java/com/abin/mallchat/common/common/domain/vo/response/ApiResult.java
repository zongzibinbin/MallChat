package com.abin.mallchat.common.common.domain.vo.response;

import com.abin.mallchat.common.common.exception.ErrorEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Description: 通用返回体
 * Author: <a href="https://github.com/zongzibinbin">abin</a>
 * Date: 2023-03-23
 */
@Data
@ApiModel("基础返回体")
@Accessors(chain = true)
public class ApiResult<T> {
    @ApiModelProperty("成功标识true or false")
    private Boolean success;
    @ApiModelProperty("错误码")
    private Integer errCode;
    @ApiModelProperty("错误消息")
    private String errMsg;
    @ApiModelProperty("返回对象")
    private T data;

    public static <T> ApiResult<T> success() {
       return new ApiResult<T>().setData(null).setSuccess(Boolean.TRUE);
    }

    public static <T> ApiResult<T> success(T data) {
        return new ApiResult<T>().setData(data).setSuccess(Boolean.TRUE);
    }

    public static <T> ApiResult<T> fail(Integer code, String msg) {
        return new ApiResult<T>().setSuccess(Boolean.FALSE).setErrCode(code).setErrMsg(msg);
    }

    public static <T> ApiResult<T> fail(ErrorEnum errorEnum) {
        return new ApiResult<T>()
                .setSuccess(Boolean.FALSE)
                .setErrCode(errorEnum.getErrorCode())
                .setErrMsg(errorEnum.getErrorMsg());
    }

    public boolean isSuccess() {
        return this.success;
    }
}
