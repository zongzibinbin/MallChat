package com.abin.mallchat.common.common.utils.sensitiveWord;

import java.util.List;

/**
 * 敏感词
 *
 * @author zhaoyuhang
 * @date 2023/07/09
 */
public interface IWordDeny {
    /**
     * 获取结果
     * @return 结果
     * @since 0.0.13
     */
    List<String> deny();
}
