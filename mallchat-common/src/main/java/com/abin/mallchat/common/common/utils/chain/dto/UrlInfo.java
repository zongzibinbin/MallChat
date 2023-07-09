package com.abin.mallchat.common.common.utils.chain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Description: 链接信息提取类
 * Author: achao
 * Date: 2023/7/6 8:54
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UrlInfo {
    /**
     *  标题
     **/
    String title;

    /**
     *  描述
     **/
    String description;

    /**
     *  网站LOGO
     **/
    String image;
}
