package com.abin.mallchat.common.common.intecepter;

import cn.hutool.core.date.StopWatch;
import cn.hutool.json.JSONUtil;
import com.abin.mallchat.common.common.constant.MDCKey;
import com.abin.mallchat.common.common.dao.WebLogDao;
import com.abin.mallchat.common.common.domain.dto.RequestInfo;
import com.abin.mallchat.common.common.domain.entity.WebLog;
import com.abin.mallchat.common.common.domain.vo.response.ApiResult;
import com.abin.mallchat.common.common.utils.RequestHolder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * 日志切面
 *
 * @author wayne
 */
@Aspect
@Slf4j
@Component
@RequiredArgsConstructor
public class WebLogAspect {

    private static final int URI_MAX_LENGTH = 255;
    private static final int CLASS_METHOD_MAX_LENGTH = 255;
    private static final int ERR_MSG_MAX_LENGTH = 512;
    private static final int EXCEPTION_NAME_MAX_LENGTH = 255;
    private static final int USER_AGENT_MAX_LENGTH = 512;
    private static final int REFERER_MAX_LENGTH = 512;

    private final WebLogDao webLogDao;

    /**
     * 接收到请求，记录请求内容
     * 所有controller包下所有的类的方法，都是切点
     * <p>
     * 如果ApiResult返回success=false，则打印warn日志；
     * warn日志只能打印在同一行，因为只有等到ApiResult结果才知道是success=false。
     * <p>
     * 如果ApiResult返回success=true，则打印info日志；
     * 特别注意：由于info级别日志已经包含了warn级别日志。如果开了info级别日志，warn就不会打印了。
     */
    @Around("execution(* com..controller..*.*(..))")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        String method = request.getMethod();
        String uri = request.getRequestURI();
        //如果参数有HttpRequest,ServletResponse，直接移除，不打印这些
        List<Object> paramList = Stream.of(joinPoint.getArgs())
                .filter(args -> !(args instanceof ServletRequest))
                .filter(args -> !(args instanceof ServletResponse))
                .collect(Collectors.toList());
        String printParamStr = paramList.size() == 1 ? JSONUtil.toJsonStr(paramList.get(0)) : JSONUtil.toJsonStr(paramList);
        RequestInfo requestInfo = RequestHolder.get();
        String userHeaderStr = JSONUtil.toJsonStr(requestInfo);
        if (log.isInfoEnabled()) {
            log.info("[{}][{}]【base:{}】【request:{}】", method, uri, userHeaderStr, printParamStr);
        }
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        Object result = null;
        Throwable throwable = null;
        try {
            result = joinPoint.proceed();
            return result;
        } catch (Throwable e) {
            throwable = e;
            throw e;
        } finally {
            stopWatch.stop();
            long cost = stopWatch.getTotalTimeMillis();
            if (Objects.isNull(throwable) && log.isInfoEnabled()) {
                String printResultStr = JSONUtil.toJsonStr(result);
                log.info("[{}]【response:{}】[cost:{}ms]", uri, printResultStr, cost);
            }
            saveWebLog(joinPoint, request, paramList, result, throwable, cost);
        }
    }

    private void saveWebLog(ProceedingJoinPoint joinPoint, HttpServletRequest request, List<Object> paramList,
                            Object result, Throwable throwable, long cost) {
        try {
            RequestInfo requestInfo = RequestHolder.get();
            WebLog.WebLogBuilder builder = WebLog.builder()
                    .traceId(MDC.get(MDCKey.TID))
                    .uid(Objects.nonNull(requestInfo) ? requestInfo.getUid() : null)
                    .ip(Objects.nonNull(requestInfo) ? requestInfo.getIp() : null)
                    .httpMethod(request.getMethod())
                    .uri(limitLength(request.getRequestURI(), URI_MAX_LENGTH))
                    .classMethod(limitLength(getClassMethod(joinPoint), CLASS_METHOD_MAX_LENGTH))
                    .requestParam(paramList.size() == 1 ? paramList.get(0) : paramList)
                    .responseResult(result)
                    .costMs(cost)
                    .userAgent(limitLength(request.getHeader("User-Agent"), USER_AGENT_MAX_LENGTH))
                    .referer(limitLength(request.getHeader("Referer"), REFERER_MAX_LENGTH));
            if (result instanceof ApiResult) {
                ApiResult<?> apiResult = (ApiResult<?>) result;
                builder.success(apiResult.getSuccess())
                        .errCode(apiResult.getErrCode())
                        .errMsg(limitLength(apiResult.getErrMsg(), ERR_MSG_MAX_LENGTH));
            }
            if (Objects.nonNull(throwable)) {
                builder.exceptionName(limitLength(throwable.getClass().getName(), EXCEPTION_NAME_MAX_LENGTH))
                        .exceptionMsg(throwable.getMessage());
            }
            webLogDao.save(builder.build());
        } catch (Exception e) {
            log.warn("save web log failed, uri:{}", request.getRequestURI(), e);
        }
    }

    private String getClassMethod(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        return joinPoint.getTarget().getClass().getSimpleName() + "#" + method.getName();
    }

    private String limitLength(String value, int maxLength) {
        if (Objects.isNull(value) || value.length() <= maxLength) {
            return value;
        }
        return value.substring(0, maxLength);
    }

}
