package com.abin.mallchat.custom.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2WebMvc;

/**
 * Description:
 * Author: <a href="https://github.com/zongzibinbin">abin</a>
 * Date: 2023-03-23
 */
@Configuration
@EnableSwagger2WebMvc
public class SwaggerConfig {
    @Bean(value = "defaultApi2")
    Docket docket() {
        return new Docket(DocumentationType.SWAGGER_2)
                //配置网站的基本信息
                .apiInfo(new ApiInfoBuilder()
                        //网站标题
                        .title("mallchat接口文档")
                        //标题后面的版本号
                        .version("v1.0")
                        .description("mallchat接口文档")
                        //联系人信息
                        .contact(new Contact("阿斌", "http://www.mallchat.cn", "972627721@qq.com"))
                        .build())
                .select()
                //指定接口的位置
                .apis(RequestHandlerSelectors
                        .withClassAnnotation(RestController.class)
                )
                .paths(PathSelectors.any())
                .build();
    }
    /**
     * swagger 配置
     * @param environment 环境
     */
//    @Bean
//    public Docket docket(Environment environment) {
//
//        // 设置环境范围
//        Profiles profiles = Profiles.of("dev","test");
//        // 如果在该环境返回内则返回：true，反之返回 false
//        boolean flag = environment.acceptsProfiles(profiles);
//
//        // 创建一个 swagger 的 bean 实例
//        return new Docket(DocumentationType.SWAGGER_2)
//                .enable(flag) // 是否开启 swagger：true -> 开启，false -> 关闭
//                ;
//    }

}
