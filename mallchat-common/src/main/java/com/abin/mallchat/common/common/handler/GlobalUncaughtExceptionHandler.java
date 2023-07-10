package com.abin.mallchat.common.common.handler;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GlobalUncaughtExceptionHandler  implements Thread.UncaughtExceptionHandler {


    @Override
    public void uncaughtException(Thread t, Throwable e) {
        log.error("Exception in thread {} ", t.getName(), e);
    }

}
