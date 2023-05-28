package com.abin.mallchat.common.common.utils.discover;

import cn.hutool.core.util.StrUtil;
import org.jsoup.nodes.Document;

import java.util.ArrayList;
import java.util.List;

/**
 * Description: 具有优先级的title查询器
 * Author: <a href="https://github.com/zongzibinbin">abin</a>
 * Date: 2023-05-27
 */
public class PrioritizedUrlTitleDiscover extends AbstractUrlTitleDiscover {

    private final List<UrlTitleDiscover> urlTitleDiscovers = new ArrayList<>(2);

    public PrioritizedUrlTitleDiscover() {
        urlTitleDiscovers.add(new CommonUrlTitleDiscover());
        urlTitleDiscovers.add(new WxUrlTitleDiscover());
    }

    @Override
    public String getDocTitle(Document document) {
        for (UrlTitleDiscover urlTitleDiscover : urlTitleDiscovers) {
            String urlTitle = urlTitleDiscover.getDocTitle(document);
            if (StrUtil.isNotBlank(urlTitle)) {
                return urlTitle;
            }
        }
        return null;
    }
}
