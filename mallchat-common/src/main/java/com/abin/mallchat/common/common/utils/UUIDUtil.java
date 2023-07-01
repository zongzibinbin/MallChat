package com.abin.mallchat.common.common.utils;

import com.github.yitter.idgen.YitIdHelper;

/**
 * Description: UUID生成器工具类
 * Author: <a href="https://www.ahao.homes">ahao: https://github.com/HowlsLee</a>
 */
public class UUIDUtil {

    public static String getId() {
        return String.valueOf(YitIdHelper.nextId());
    }
}
