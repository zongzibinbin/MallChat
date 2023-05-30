package com.abin.mallchat.common.common.factory;

import com.abin.mallchat.common.common.handler.GlobalUncaughtExceptionHandler;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ThreadFactory;

@Slf4j
@AllArgsConstructor
@NoArgsConstructor
public class MyThreadFactory   implements ThreadFactory {

  private String name;


    @Override
    public Thread newThread(Runnable r) {
        Thread thread = new Thread(r);
        thread.setUncaughtExceptionHandler(new GlobalUncaughtExceptionHandler(name));
        return thread;
    }
}
