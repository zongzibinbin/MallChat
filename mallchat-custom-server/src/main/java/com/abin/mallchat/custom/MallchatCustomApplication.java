package com.abin.mallchat.custom;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

/**
 * <p>
 * Mallchat C端 主启动程序
 * </p>
 *
 * @author <a href="https://github.com/zongzibinbin">zhongzb</a>
 * @since 2021-05-27
 */
@SpringBootApplication(scanBasePackages = {"com.abin.mallchat"})
@MapperScan({"com.abin.mallchat.common.**.mapper"})
@ServletComponentScan
public class MallchatCustomApplication {

    public static void main(String[] args) {
        SpringApplication.run(MallchatCustomApplication.class);
    }

}