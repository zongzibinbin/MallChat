package com.abin.mallchat.common.common.handler;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GlobalUncaughtExceptionHandler  implements Thread.UncaughtExceptionHandler {
    private String name;
    public GlobalUncaughtExceptionHandler(){}
    public GlobalUncaughtExceptionHandler(String name){
        this.name=name;
    }
    @Override
    public void uncaughtException(Thread t, Throwable e) {
        log.error("current thread ",t.getName()," is error[{}]",e);
    }

}
