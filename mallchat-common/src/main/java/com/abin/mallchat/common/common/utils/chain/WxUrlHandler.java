package com.abin.mallchat.common.common.utils.chain;

import cn.hutool.core.util.StrUtil;
import org.jetbrains.annotations.Nullable;
import org.jsoup.nodes.Document;

/**
 * Description:
 * Author: achao
 * Date: 2023/7/6 9:34
 */
public class WxUrlHandler extends FactoryUrlHandler {

    @Nullable
    @Override
    public String getTitle(Document document) {
        return document.getElementsByAttributeValue("property", "og:title").attr("content");
    }

    @Nullable
    @Override
    public String getDescription(Document document) {
        String description = document.getElementsByAttributeValue("property", "og:description").attr("content");
        return StrUtil.isNotBlank(description) ? description.substring(0, description.indexOf("。")) : description;
    }

    @Nullable
    @Override
    public String getImage(String url, Document document) {
        return document.getElementsByAttributeValue("property", "og:image").attr("content");
    }
}
