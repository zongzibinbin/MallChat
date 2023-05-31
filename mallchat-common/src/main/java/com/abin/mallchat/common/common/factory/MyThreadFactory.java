package com.abin.mallchat.common.common.factory;

import com.abin.mallchat.common.common.handler.GlobalUncaughtExceptionHandler;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadFactory;

@Slf4j
@AllArgsConstructor
public class MyThreadFactory  implements ThreadFactory {

  private ThreadFactory factory;

    @Override
    public Thread newThread(Runnable r) {
        Thread thread =factory.newThread(r);
        thread.setUncaughtExceptionHandler(new GlobalUncaughtExceptionHandler());
        thread.setDaemon(false);
        thread.setPriority(5);
        return thread;
    }
}
