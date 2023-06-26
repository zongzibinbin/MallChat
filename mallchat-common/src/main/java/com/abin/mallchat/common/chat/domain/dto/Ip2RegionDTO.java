package com.abin.mallchat.common.chat.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Description: 离线获取ip归属信息返回
 * Author: coderwaves
 * Date: 2023-06-26
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Ip2RegionDTO {
    private String county;
    private String province;
    private String city;
    private String lsp;
}
