package com.abin.mallchat.transaction.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 保证方法成功执行。如果在事务内的方法，会将操作记录入库，保证执行。
 */
@Retention(RetentionPolicy.RUNTIME)//运行时生效
@Target(ElementType.METHOD)//作用在方法上
public @interface SecureInvoke {

    /**
     * 默认3次
     *
     * @return 最大重试次数(包括第一次正常执行)
     */
    int maxRetryTimes() default 3;

    /**
     * 默认异步执行，先入库，后续异步执行，不影响主线程快速返回结果,毕竟失败了有重试，而且主线程的事务已经提交了，串行执行没啥意义。
     * 同步执行适合mq消费场景等对耗时不关心，但是希望链路追踪不被异步影响的场景。
     *
     * @return 是否异步执行
     */
    boolean async() default true;
}
