package com.abin.mallchat.common.common.factory;

import com.abin.mallchat.common.utils.MDCUtils;
import org.springframework.core.task.TaskDecorator;

/**
 * MDC任务装饰器，用于在线程池执行任务时传递MDC上下文
 */
public class MDCTaskDecorator implements TaskDecorator {

    @Override
    public Runnable decorate(Runnable runnable) {
        return MDCUtils.wrap(runnable);
    }
}
