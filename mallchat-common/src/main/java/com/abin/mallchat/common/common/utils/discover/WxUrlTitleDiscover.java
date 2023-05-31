package com.abin.mallchat.common.common.utils.discover;

import org.jsoup.nodes.Document;

/**
 * <p>
 * 针对微信公众号文章的标题获取类
 * </p>
 *
 * @author <a href="https://github.com/zongzibinbin">abin</a>
 * @since 2023-05-27
 */
public class WxUrlTitleDiscover extends AbstractUrlTitleDiscover {

    @Override
    public String getDocTitle(Document document) {
        return document.getElementsByAttributeValue("property", "og:title").attr("content");
    }

}
