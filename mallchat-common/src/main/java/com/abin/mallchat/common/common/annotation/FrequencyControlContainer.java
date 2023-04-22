package com.abin.mallchat.common.common.annotation;

import java.lang.annotation.*;


@Retention(RetentionPolicy.RUNTIME)//运行时生效
@Target(ElementType.METHOD)//作用在方法上
public @interface FrequencyControlContainer {
    FrequencyControl[] value();
}
