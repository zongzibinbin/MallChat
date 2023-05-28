package com.abin.mallchat.common.common.utils.discover;

import org.jsoup.nodes.Document;

/**
 * Description: 针对微信公众号文章的标题获取类
 * Author: <a href="https://github.com/zongzibinbin">abin</a>
 * Date: 2023-05-27
 */
public class WxUrlTitleDiscover extends AbstractUrlTitleDiscover {
    @Override
    public String getDocTitle(Document document) {
        return document.getElementsByAttributeValue("property", "og:title").attr("content");
    }
}
