package com.abin.mallchat.custom.common.intecepter;

import com.abin.mallchat.common.common.constant.MDCKey;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.*;
import javax.servlet.FilterConfig;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.UUID;

/**
 * Description: 设置链路追踪的值，初期单体项目先简单用
 * Author: <a href="https://github.com/zongzibinbin">abin</a>
 * Date: 2023-04-05
 */
@Slf4j
@WebFilter(urlPatterns = "/*")
public class HttpTraceIdFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        String tid = UUID.randomUUID().toString();
        MDC.put(MDCKey.TID, tid);
        chain.doFilter(request, response);
    }

}
