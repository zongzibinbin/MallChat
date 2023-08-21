package com.abin.mallchat.common.common.intecepter;

import cn.hutool.core.date.StopWatch;
import cn.hutool.json.JSONUtil;
import com.abin.mallchat.common.common.domain.dto.RequestInfo;
import com.abin.mallchat.common.common.utils.RequestHolder;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
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
public class WebLogAspect {


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
        Object result = joinPoint.proceed();
        stopWatch.stop();
        long cost = stopWatch.getTotalTimeMillis();
        String printResultStr = JSONUtil.toJsonStr(result);
        if (log.isInfoEnabled()) {
            log.info("[{}]【response:{}】[cost:{}ms]", uri, printResultStr, cost);
        }
        return result;
    }


}