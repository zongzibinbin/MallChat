package com.abin.mallchat.common.common.handler;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GlobalUncaughtExceptionHandler  implements Thread.UncaughtExceptionHandler {


    @Override
    public void uncaughtException(Thread t, Throwable e) {
        log.error("{} task  execute is error",t.getName());
        e.printStackTrace();
    }

}
