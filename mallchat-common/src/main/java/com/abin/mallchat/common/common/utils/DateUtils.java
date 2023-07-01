package com.abin.mallchat.common.common.utils;

import java.util.Calendar;
import java.util.Date;

public class DateUtils extends org.apache.commons.lang3.time.DateUtils {
    public static Long getEndTimeByToday() {
        Calendar instance = Calendar.getInstance();
        Date now = new Date();
        instance.setTime(now);
        instance.set(Calendar.HOUR_OF_DAY, 23);
        instance.set(Calendar.MINUTE, 59);
        instance.set(Calendar.SECOND, 59);
        return instance.getTime().getTime() - now.getTime();
    }
}
