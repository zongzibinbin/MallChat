package com.abin.mallchat.custom.user.controller;

import cn.hutool.core.util.StrUtil;
import com.abin.mallchat.common.chat.domain.entity.McEmojis;
import com.abin.mallchat.common.common.domain.vo.request.CursorPageBaseReq;
import com.abin.mallchat.common.common.domain.vo.response.ApiResult;
import com.abin.mallchat.common.common.domain.vo.response.CursorPageBaseResp;
import com.abin.mallchat.common.common.utils.RequestHolder;
import com.abin.mallchat.custom.user.service.EmojisService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * 用户表情包
 *
 * @author: WuShiJie
 * @createTime: 2023/7/3 14:21
 */
@RestController
@RequestMapping("/capi/emojis")
@Api(tags = "用户表情包管理相关接口")
public class EmojisController {

    /**
     * 用户表情包 Service
     */
    @Resource
    private EmojisService emojisService;


    /**
     * 表情包列表
     *
     * @param request 游标翻页请求参数
     * @return 表情包列表
     * @author WuShiJie
     * @createTime 2023/7/3 14:46
     **/
    @GetMapping("/getEmojisPage")
    @ApiOperation("表情包列表")
    public ApiResult<CursorPageBaseResp<McEmojis>> getEmojisPage(@Valid CursorPageBaseReq request) {
        return ApiResult.success(emojisService.getEmojisPage(request, RequestHolder.get().getUid()));
    }


    /**
     * 新增表情包
     *
     * @param emojis 用户表情包
     * @return 表情包
     * @author WuShiJie
     * @createTime 2023/7/3 14:46
     **/
    @PostMapping("/insertEmojis")
    @ApiOperation("表情包列表")
    public ApiResult<McEmojis> insertEmojis(@Valid @RequestBody McEmojis emojis) {
        emojis.setUserId(RequestHolder.get().getUid());
        LambdaQueryWrapper<McEmojis> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(McEmojis::getUserId,emojis.getUserId());
        queryWrapper.eq(McEmojis::getExpressionUrl,emojis.getExpressionUrl());
        emojisService.saveOrUpdate(emojis, queryWrapper);
        return ApiResult.success(emojis);
    }

    /**
     * 删除表情包
     *
     * @param id 用户表情包ID
     * @return 删除结果
     * @author WuShiJie
     * @createTime 2023/7/3 14:46
     **/
    @GetMapping("/deleteEmojis")
    @ApiOperation("表情包列表")
    public ApiResult<Void> deleteEmojis(@RequestParam("id") String id) {
        emojisService.removeById(id);
        return ApiResult.success();
    }
}
