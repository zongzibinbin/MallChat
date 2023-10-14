package com.abin.mallchat.common.common.utils.discover;

import cn.hutool.core.date.StopWatch;
import com.abin.mallchat.common.common.utils.discover.domain.UrlInfo;
import org.jsoup.nodes.Document;

import javax.annotation.Nullable;
import java.util.Map;

/**
 * @author zhaoqichao
 * @date 2023/7/3 16:34
 */
public interface UrlDiscover {


    @Nullable
    Map<String, UrlInfo> getUrlContentMap(String content);

    @Nullable
    UrlInfo getContent(String url);

    @Nullable
    String getTitle(Document document);

    @Nullable
    String getDescription(Document document);

    @Nullable
    String getImage(String url, Document document);

    public static void main(String[] args) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        String longStr = "其中包含一个URL www.baidu.com,一个带有端口号的URL http://www.jd.com:80, 一个带有路径的URL http://mallchat.cn, 还有美团技术文章https://mp.weixin.qq.com/s/hwTf4bDck9_tlFpgVDeIKg ";
//        String longStr = "一个带有端口号的URL http://www.jd.com:80,";
//        String longStr = "一个带有路径的URL http://mallchat.cn";
        PrioritizedUrlDiscover discover = new PrioritizedUrlDiscover();
        final Map<String, UrlInfo> map = discover.getUrlContentMap(longStr);
        System.out.println(map);
        stopWatch.stop();
        long cost = stopWatch.getTotalTimeMillis();
        System.out.println(cost);
    }
}
