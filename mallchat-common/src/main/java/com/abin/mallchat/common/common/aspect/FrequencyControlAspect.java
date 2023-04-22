package com.abin.mallchat.common.common.aspect;

import cn.hutool.core.util.StrUtil;
import com.abin.mallchat.common.common.annotation.FrequencyControl;
import com.abin.mallchat.common.common.exception.BusinessException;
import com.abin.mallchat.common.common.exception.CommonErrorEnum;
import com.abin.mallchat.common.common.utils.RedisUtils;
import com.abin.mallchat.common.common.utils.RequestHolder;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Description: 频控实现
 * Author: <a href="https://github.com/zongzibinbin">abin</a>
 * Date: 2023-04-20
 */
@Slf4j
@Aspect
@Component
public class FrequencyControlAspect {
    @Autowired
    private RedisUtils redisUtils;
    private final ExpressionParser parser = new SpelExpressionParser();
    private final DefaultParameterNameDiscoverer parameterNameDiscoverer = new DefaultParameterNameDiscoverer();

    @Around("@annotation(com.abin.mallchat.common.common.annotation.FrequencyControl)||@annotation(com.abin.mallchat.common.common.annotation.FrequencyControlContainer)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        FrequencyControl[] annotationsByType = method.getAnnotationsByType(FrequencyControl.class);
        Map<String, FrequencyControl> keyMap = new HashMap<>();
        for (int i = 0; i < annotationsByType.length; i++) {
            FrequencyControl frequencyControl = annotationsByType[i];
            String prefix = StrUtil.isBlank(frequencyControl.prefixKey()) ? method.toGenericString() + ":index:" + i : frequencyControl.prefixKey();//默认方法限定名+注解排名（可能多个）
            String key = "";
            switch (frequencyControl.target()) {
                case EL:
                    key = parseSpEl(method, joinPoint.getArgs(), frequencyControl.spEl());
                    break;
                case IP:
                    key = RequestHolder.get().getIp();
                    break;
                case UID:
                    key = RequestHolder.get().getUid().toString();
            }
            keyMap.put(prefix + ":" + key, frequencyControl);
        }
        //批量获取redis统计的值
        ArrayList<String> keyList = new ArrayList<>(keyMap.keySet());
        List<Integer> countList = redisUtils.mget(keyList, Integer.class);
        for (int i = 0; i < keyList.size(); i++) {
            String key = keyList.get(i);
            Integer count = countList.get(i);
            FrequencyControl frequencyControl = keyMap.get(key);
            if (Objects.nonNull(count) && count >= frequencyControl.count()) {//频率超过了
                log.warn("frequencyControl limit key:{},count:{}", key, count);
                throw new BusinessException(CommonErrorEnum.FREQUENCY_LIMIT);
            }
        }
        try {
            return joinPoint.proceed();
        } finally {
            //不管成功还是失败，都增加次数
            keyMap.forEach((k, v) -> {
                redisUtils.inc(k,v.time(),v.unit());
            });
        }
    }

    private String parseSpEl(Method method, Object[] args, String spEl) {
        String[] params = parameterNameDiscoverer.getParameterNames(method);//解析参数名
        EvaluationContext context = new StandardEvaluationContext();//el解析需要的上下文对象
        for (int i = 0; i < params.length; i++) {
            context.setVariable(params[i], args[i]);//所有参数都作为原材料扔进去
        }
        Expression expression = parser.parseExpression(spEl);
        return expression.getValue(context, String.class);
    }
}
