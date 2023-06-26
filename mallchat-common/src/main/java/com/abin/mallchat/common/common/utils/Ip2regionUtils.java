package com.abin.mallchat.common.common.utils;

import com.abin.mallchat.common.chat.domain.dto.Ip2RegionDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.lionsoul.ip2region.xdb.Searcher;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.util.Objects;

/**
 * Description: 离线获取ip归属信息
 * Author: coderwaves
 * Date: 2023-06-26
 */
@Slf4j
public class Ip2regionUtils {

    private static final Searcher searcher;

    static {
        Resource classPathResource = new ClassPathResource("ip2regiondb/ip2region.xdb");
        try {
            byte[] cBuff = IOUtils.toByteArray(classPathResource.getInputStream());
            searcher = Searcher.newWithBuffer(cBuff);
        } catch (Exception e) {
            log.error("failed to create content cached searcher",e);
            throw new RuntimeException("Failed to create content cached searcher", e);
        }
    }

    public static Ip2RegionDTO ip2region(String ip) {
        try {
            String region = searcher.search(ip);
            log.info("ip2region search [{}]  [{}]",ip,region);
            String[] fields = region.split("\\|");
            Ip2RegionDTO ip2RegionDTO = new Ip2RegionDTO();
            ip2RegionDTO.setCounty(fields[0]);
            ip2RegionDTO.setProvince(fields[2]);
            ip2RegionDTO.setCity(fields[3]);
            ip2RegionDTO.setLsp(fields[4]);
            return ip2RegionDTO;
        } catch (Exception e) {
            log.error("failed to search [{}]",ip,e);
            //调用方自行切换其他获取方式
            return null;
        }
    }

    public static void main(String[] args) {
        Ip2RegionDTO ip2RegionDTO = ip2region("182.130.233.60");
        if(Objects.isNull(ip2RegionDTO)){
            //使用其他方式获取
        }else {
            System.err.println(ip2RegionDTO);
        }
    }
}
