package com.abin.frequencycontrol.annotation;

import com.abin.mallchat.common.FrequencyControlConstant;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;


/**
 * 频控注解
 */
@Repeatable(FrequencyControlContainer.class) // 可重复
@Retention(RetentionPolicy.RUNTIME)// 运行时生效
@Target(ElementType.METHOD)//作用在方法上
public @interface FrequencyControl {
    /**
     * 策略
     */
    String strategy() default FrequencyControlConstant.TOTAL_COUNT_WITH_IN_FIX_TIME;

    /**
     * 窗口大小，默认 5 个 period
     */
    int windowSize() default 5;

    /**
     * 窗口最小周期 1s (窗口大小是 5s， 1s一个小格子，共10个格子)
     */
    int period() default 1;


    /**
     * key的前缀，默认取方法全限定名，除非我们在不同方法上对同一个资源做频控，就自己指定
     *
     * @return key的前缀
     */
    String prefixKey() default "";

    /**
     * 频控对象，默认el表达指定具体的频控对象
     * 对于ip 和uid模式，需要是http入口的对象，保证RequestHolder里有值
     *
     * @return 对象
     */
    Target target() default Target.EL;

    /**
     * springEl 表达式，target=EL必填
     *
     * @return 表达式
     */
    String spEl() default "";

    /**
     * 频控时间范围，默认单位秒
     *
     * @return 时间范围
     */
    int time() default 10;

    /**
     * 频控时间单位，默认秒
     *
     * @return 单位
     */
    TimeUnit unit() default TimeUnit.SECONDS;

    /**
     * 单位时间内最大访问次数
     *
     * @return 次数
     */
    int count() default 1;

    long capacity() default 3; // 令牌桶容量

    double refillRate() default 0.5; // 每秒补充的令牌数

    enum Target {
        UID,
        IP,
        EL
    }
}
