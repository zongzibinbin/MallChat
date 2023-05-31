package com.abin.mallchat.common.common.utils.discover;

import org.jsoup.nodes.Document;

/**
 * <p>
 * 通用的标题解析类
 * </p>
 *
 * @author <a href="https://github.com/zongzibinbin">abin</a>
 * @since 2023-05-27
 */
public class CommonUrlTitleDiscover extends AbstractUrlTitleDiscover {

    @Override
    public String getDocTitle(Document document) {
        return document.title();
    }

}
