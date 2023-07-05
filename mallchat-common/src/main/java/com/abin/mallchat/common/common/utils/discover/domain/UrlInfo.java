package com.abin.mallchat.common.common.utils.discover.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zhaoqichao
 * @date 2023/7/3 16:12
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
