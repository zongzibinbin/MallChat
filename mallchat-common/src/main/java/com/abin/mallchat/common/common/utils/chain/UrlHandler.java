package com.abin.mallchat.common.common.utils.chain;

import com.abin.mallchat.common.common.utils.discover.domain.UrlInfo;
import org.jsoup.nodes.Document;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Description: url集合处理抽象接口定义类
 * Author: achao
 * Date: 2023/7/6 8:58
 */
public abstract class UrlHandler {

    /**
     * 提取消息中的所有链接，并组装Map
     * @param content
     * @return
     */
    @Nullable
    abstract Map<String,UrlInfo> getUrlContentMap(String content);

}
