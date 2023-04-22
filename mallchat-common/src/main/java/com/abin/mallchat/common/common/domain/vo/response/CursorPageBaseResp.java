package com.abin.mallchat.common.common.domain.vo.response;

import cn.hutool.core.collection.CollectionUtil;
import com.abin.mallchat.common.chat.domain.entity.Message;
import com.abin.mallchat.common.common.domain.vo.request.CursorPageBaseReq;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="https://github.com/zongzibinbin">abin</a>
 * @since 2023-03-19
 */
@Data
@ApiModel("游标翻页返回")
@AllArgsConstructor
@NoArgsConstructor
public class CursorPageBaseResp<T> {

    @ApiModelProperty("游标（下次翻页带上这参数）")
    private String cursor;

    @ApiModelProperty("是否最后一页")
    private Boolean isLast = Boolean.FALSE;

    @ApiModelProperty("数据列表")
    private List<T> list;

    public static CursorPageBaseResp init(CursorPageBaseResp cursorPage, List list) {
        CursorPageBaseResp cursorPageBaseResp = new CursorPageBaseResp();
        cursorPageBaseResp.setIsLast(cursorPage.getIsLast());
        cursorPageBaseResp.setList(list);
        cursorPageBaseResp.setCursor(cursorPage.getCursor());
        return cursorPageBaseResp;
    }
    @JsonIgnore
    public Boolean isEmpty() {
        return CollectionUtil.isEmpty(list);
    }

    public static CursorPageBaseResp empty() {
        CursorPageBaseResp cursorPageBaseResp = new CursorPageBaseResp();
        cursorPageBaseResp.setIsLast(true);
        cursorPageBaseResp.setList(new ArrayList());
        return cursorPageBaseResp;
    }

}
