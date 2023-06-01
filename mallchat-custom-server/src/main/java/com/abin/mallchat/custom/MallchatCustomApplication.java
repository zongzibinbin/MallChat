package com.abin.mallchat.custom;

import com.abin.mallchat.common.common.utils.SpringContextHolder;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.Bean;

/**
 * @author zhongzb
 * @date 2021/05/27
 */
@SpringBootApplication(scanBasePackages = {"com.abin.mallchat"})
@MapperScan({"com.abin.mallchat.common.**.mapper"})
@ServletComponentScan
public class MallchatCustomApplication {

    public static void main(String[] args) {
        SpringApplication.run(MallchatCustomApplication.class);
    }


    @Bean
    public SpringContextHolder springContextHolder(){
        return new SpringContextHolder();
    }

}