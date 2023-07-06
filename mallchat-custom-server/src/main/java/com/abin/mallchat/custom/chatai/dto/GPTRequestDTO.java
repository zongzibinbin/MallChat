package com.abin.mallchat.custom.chatai.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GPTRequestDTO {
    /**
     * 聊天内容
     */
    private String content;
    /**
     * 用户Id
     */
    private Long uid;
}
