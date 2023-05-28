package com.abin.mallchat.common.common.utils.discover;

import org.jsoup.nodes.Document;

/**
 * Description: 通用的标题解析类
 * Author: <a href="https://github.com/zongzibinbin">abin</a>
 * Date: 2023-05-27
 */
public class CommonUrlTitleDiscover extends AbstractUrlTitleDiscover {
    @Override
    public String getDocTitle(Document document) {
        return document.title();
    }
}
