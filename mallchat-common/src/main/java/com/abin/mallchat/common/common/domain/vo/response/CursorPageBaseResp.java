package com.abin.mallchat.common.common.domain.vo.response;

import cn.hutool.core.collection.CollectionUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Collections;
import java.util.List;

/**
 * @author <a href="https://github.com/zongzibinbin">abin</a>
 * @since 2023-03-19
 */
@Data
@ApiModel("游标翻页返回")
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class CursorPageBaseResp<T> {

    @ApiModelProperty("游标（下次翻页带上这参数）")
    private String cursor;

    @ApiModelProperty("是否最后一页")
    private Boolean isLast = Boolean.FALSE;

    @ApiModelProperty("数据列表")
    private List<T> list;

    public static <T> CursorPageBaseResp<T> init(CursorPageBaseResp cursorPage, List<T> list) {
        return new CursorPageBaseResp<T>()
                .setIsLast(cursorPage.getIsLast())
                .setList(list)
                .setCursor(cursorPage.getCursor());
    }

    @JsonIgnore
    public Boolean isEmpty() {
        return CollectionUtil.isEmpty(list);
    }

    public static <T> CursorPageBaseResp<T> empty() {
        return new CursorPageBaseResp<T>().setIsLast(true).setList(Collections.emptyList());
    }

}
