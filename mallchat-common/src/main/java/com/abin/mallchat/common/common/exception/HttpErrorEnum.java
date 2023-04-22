package com.abin.mallchat.common.common.exception;

import cn.hutool.http.ContentType;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.abin.mallchat.common.common.domain.vo.response.ApiResult;
import com.google.common.base.Charsets;
import io.netty.handler.codec.http.HttpContent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.CharSet;
import org.apache.commons.lang3.CharSetUtils;
import org.springframework.http.HttpStatus;
import sun.awt.CharsetString;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.Charset;

/**
 * Description: 业务校验异常码
 * Author: <a href="https://github.com/zongzibinbin">abin</a>
 * Date: 2023-03-26
 */
@AllArgsConstructor
@Getter
public enum HttpErrorEnum implements ErrorEnum {
    ACCESS_DENIED(401, "登录失效，请重新登录"),
    ;
    private Integer httpCode;
    private String msg;

    @Override
    public Integer getErrorCode() {
        return httpCode;
    }

    @Override
    public String getErrorMsg() {
        return msg;
    }

    public void sendHttpError(HttpServletResponse response) throws IOException {
        response.setStatus(this.getErrorCode());
        ApiResult responseData = ApiResult.fail(this);
        response.setContentType(ContentType.JSON.toString(Charsets.UTF_8));
        response.getWriter().write(JSONUtil.toJsonStr(responseData));
    }
}
