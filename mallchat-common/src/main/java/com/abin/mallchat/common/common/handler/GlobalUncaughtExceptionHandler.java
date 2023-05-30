package com.abin.mallchat.common.common.handler;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
@NoArgsConstructor
public class GlobalUncaughtExceptionHandler  implements Thread.UncaughtExceptionHandler {
    private String name;

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        log.error("线程池名称：[{}],错误信息如下:",name);
        e.printStackTrace();
    }

}
