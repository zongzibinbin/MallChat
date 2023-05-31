package com.abin.mallchat.custom.common.intecepter;

import com.abin.mallchat.common.common.constant.MDCKey;
import com.abin.mallchat.common.common.exception.HttpErrorEnum;
import com.abin.mallchat.custom.user.service.LoginService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;
import java.util.Optional;

/**
 * <p>
 * token 校验拦截
 * </p>
 *
 * @author <a href="https://github.com/zongzibinbin">abin</a>
 * @since 2023-04-05
 */
@Order(-2)
@Slf4j
@Component
public class TokenInterceptor implements HandlerInterceptor {

    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String AUTHORIZATION_SCHEMA = "Bearer ";
    public static final String ATTRIBUTE_UID = "uid";

    @Autowired
    private LoginService loginService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //获取用户登录token
        String token = getToken(request);
        Long validUid = loginService.getValidUid(token);
        //有登录态
        if (Objects.nonNull(validUid)) {
            request.setAttribute(ATTRIBUTE_UID, validUid);
        } else {
            boolean isPublicURI = isPublicURI(request.getRequestURI());
            //又没有登录态，又不是公开路径，直接401
            if (!isPublicURI) {
                HttpErrorEnum.ACCESS_DENIED.sendHttpError(response);
                return false;
            }
        }
        MDC.put(MDCKey.UID, String.valueOf(validUid));
        return true;
    }

    /**
     * 判断是不是公共方法，可以未登录访问的
     *
     * @param requestURI
     */
    private boolean isPublicURI(String requestURI) {
        String[] split = requestURI.split("/");
        return split.length > 2 && "public".equals(split[3]);
    }

    private String getToken(HttpServletRequest request) {
        String header = request.getHeader(AUTHORIZATION_HEADER);
        return Optional.ofNullable(header)
                .filter(h -> h.startsWith(AUTHORIZATION_SCHEMA))
                .map(h -> h.substring(AUTHORIZATION_SCHEMA.length()))
                .orElse(null);
    }

}