package com.abin.mallchat.common.common.utils.discover;

import cn.hutool.core.date.StopWatch;
import com.google.common.base.Stopwatch;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import javax.annotation.Nullable;
import javax.annotation.Signed;
import java.util.Map;

public interface UrlTitleDiscover {


    @Nullable
    Map<String, String> getContentTitleMap(String content);


    @Nullable
    String getUrlTitle(String url);

    @Nullable
    String getDocTitle(Document document);

    public static void main(String[] args) {//用异步多任务查询并合并 974 //串行访问的速度1349  1291  1283 1559
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        String longStr = "这是一个很长的字符串再来 www.github.com，其中包含一个URL www.baidu.com,, 一个带有端口号的URL http://www.jd.com:80, 一个带有路径的URL http://mallchat.cn, 还有美团技术文章https://mp.weixin.qq.com/s/hwTf4bDck9_tlFpgVDeIKg ";
        PrioritizedUrlTitleDiscover discover =new PrioritizedUrlTitleDiscover();
        Map<String, String> contentTitleMap = discover.getContentTitleMap(longStr);
        System.out.println(contentTitleMap);
//
//        Jsoup.connect("http:// www.github.com");
        stopWatch.stop();
        long cost = stopWatch.getTotalTimeMillis();
        System.out.println(cost);
    }//{http://mallchat.cn=MallChat, www.baidu.com=百度一下，你就知道, https://mp.weixin.qq.com/s/hwTf4bDck9_tlFpgVDeIKg=超大规模数据库集群保稳系列之二：数据库攻防演练建设实践, http://www.jd.com:80=京东(JD.COM)-正品低价、品质保障、配送及时、轻松购物！}
}
