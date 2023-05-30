package com.abin.mallchat.common.common.factory;

import com.abin.mallchat.common.common.handler.MyUncaughtExceptionHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;

import java.util.concurrent.ThreadFactory;

@Slf4j
public class MyThreadFactory   implements ThreadFactory {

  private String name;
  public MyThreadFactory(String name){
      this.name=name;
    }
    public MyThreadFactory(){}

    @Override
    public Thread newThread(Runnable r) {
        Thread thread = new Thread(r);
        thread.setUncaughtExceptionHandler(new MyUncaughtExceptionHandler(name));
        return thread;
    }
}
