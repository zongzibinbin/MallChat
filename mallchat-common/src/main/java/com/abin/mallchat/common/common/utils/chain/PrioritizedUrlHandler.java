package com.abin.mallchat.common.common.utils.chain;

import cn.hutool.core.util.StrUtil;
import com.abin.mallchat.common.common.utils.discover.UrlDiscover;
import com.abin.mallchat.common.common.utils.discover.domain.UrlInfo;
import org.jetbrains.annotations.Nullable;
import org.jsoup.nodes.Document;

import java.util.List;
import java.util.Map;

/**
 * Description: 优先级链接统一处理扩展类
 * Author: achao
 * Date: 2023/7/6 9:36
 */
public class PrioritizedUrlHandler extends FactoryUrlHandler {

    private final FactoryUrlHandler commonUrlHandler = new CommonUrlHandler();
    private final FactoryUrlHandler wxUrlHandler = new WxUrlHandler();

    @Nullable
    @Override
    String getTitle(Document document) {
        return StrUtil.isBlank(wxUrlHandler.getTitle(document)) ? commonUrlHandler.getTitle(document) : wxUrlHandler.getTitle(document);
    }

    @Nullable
    @Override
    String getDescription(Document document) {
        return StrUtil.isBlank(wxUrlHandler.getDescription(document)) ? commonUrlHandler.getDescription(document) : wxUrlHandler.getDescription(document);
    }

    @Nullable
    @Override
    String getImage(String url, Document document) {
        return StrUtil.isBlank(wxUrlHandler.getImage(url, document)) ? commonUrlHandler.getImage(url, document) : wxUrlHandler.getImage(url, document);
    }
}
