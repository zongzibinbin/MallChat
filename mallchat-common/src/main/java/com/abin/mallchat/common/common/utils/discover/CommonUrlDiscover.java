package com.abin.mallchat.common.common.utils.discover;

import cn.hutool.core.util.StrUtil;
import io.jsonwebtoken.lang.Objects;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

/**
 * @author zhaoqichao
 * @date 2023/7/3 16:54
 */
public class CommonUrlDiscover extends AbstractUrlDiscover {
    @Nullable
    @Override
    public String getTitle(Document document) {
        return document.title();
    }

    @Nullable
    @Override
    public String getDescription(Document document) {
        String description = document.head().select("meta[name=description]").attr("content");
        String keywords = document.head().select("meta[name=keywords]").attr("content");
        String content = StrUtil.isNotBlank(description) ? description : keywords;
        //只保留一句话的描述
        return StrUtil.isNotBlank(content) ? content.substring(0, content.indexOf("。")) : content;
    }

    @Nullable
    @Override
    public String getImage(String url, Document document) {
        String image = document.select("link[type=image/x-icon]").attr("href");
        //如果没有去匹配含有icon属性的logo
        String href = StrUtil.isEmpty(image) ? document.select("link[rel$=icon]").attr("href") : image;
        //如果icon中已经包含了url部分域名
        if (StrUtil.isNotBlank(StrUtil.removeAny(StrUtil.removeAny(href, "/"), "favicon.ico")) &&
                StrUtil.containsAny(StrUtil.removePrefix(url, "http://"), StrUtil.removeAny(StrUtil.removeAny(href, "/"), "favicon.ico"))) {
            return "http://" + StrUtil.removePrefix(href, "/");
        }
        //如果url已经包含了logo
        if (StrUtil.containsAny(url, "favicon")) {
            return url;
        }
        //如果logo中有url
        if (StrUtil.containsAny(href, "http") || StrUtil.containsAny(href, "https")) {
            return href;
        }
        return StrUtil.format("{}/{}", url, StrUtil.removePrefix(href, "/"));
    }
}
