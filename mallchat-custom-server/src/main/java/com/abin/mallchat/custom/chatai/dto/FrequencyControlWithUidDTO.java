package com.abin.mallchat.custom.chatai.dto;

import com.abin.mallchat.common.common.domain.dto.FrequencyControlDTO;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FrequencyControlWithUidDTO extends FrequencyControlDTO {
    /**
     * 用户Id
     */
    private Long uid;
    /**
     * 剩余时间
     */
    private Long remainingMinutes;
    /**
     * 聊天次数
     */
    private Long chatNum;
}
