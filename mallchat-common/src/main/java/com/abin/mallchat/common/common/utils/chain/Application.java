package com.abin.mallchat.common.common.utils.chain;

import com.abin.mallchat.common.common.utils.discover.domain.UrlInfo;
import org.jetbrains.annotations.Nullable;
import org.jsoup.nodes.Document;

import java.util.Map;

/**
 * Description: 测试
 * Author: achao
 * Date: 2023/7/6 9:29
 */
public class Application {
    public static void main(String[] args) {
        PrioritizedUrlHandler handler = new PrioritizedUrlHandler();
        String longStr = "其中包含一个URL www.baidu.com,一个带有端口号的URL http://www.jd.com:80, 一个带有路径的URL http://mallchat.cn, 还有美团技术文章https://mp.weixin.qq.com/s/hwTf4bDck9_tlFpgVDeIKg ";

        Map<String, UrlInfo> urlContentMap = handler.getUrlContentMap(longStr);
        System.out.println(urlContentMap);
    }
}
