package com.beelego;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * quartz ui configuration
 *
 *
 * @author : hama
 * @since : created in  2018/9/21
 */
@Slf4j
@Configuration
public class QuartzUIAutoConfig implements WebMvcConfigurer{

    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("quartz/index.html")
                .addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("vue/")
                .addResourceLocations("classpath:/META-INF/resources/");
    }
}
