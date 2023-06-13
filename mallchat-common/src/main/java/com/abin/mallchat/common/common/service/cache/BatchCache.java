package com.abin.mallchat.common.common.service.cache;

import java.util.List;
import java.util.Map;

public interface BatchCache<IN, OUT> {
    /**
     * 获取单个
     */
    OUT get(IN req);

    /**
     * 获取批量
     */
    Map<IN, OUT> getBatch(List<IN> req);
}
