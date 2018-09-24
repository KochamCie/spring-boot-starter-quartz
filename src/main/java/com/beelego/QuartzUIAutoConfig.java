package com.beelego;

import com.beelego.properties.QuartzProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * quartz ui configuration
 *
 * @author : hama
 * @since : created in  2018/9/21
 */
@Slf4j
@EnableConfigurationProperties(QuartzProperties.class)
public class QuartzUIAutoConfig implements WebMvcConfigurer {

    @Autowired
    private QuartzProperties quartzProperties;

    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        if (quartzProperties.isEnabled()) {
            registry.addResourceHandler("quartz/index.html")
                    .addResourceLocations("classpath:/META-INF/resources/");
            registry.addResourceHandler("vue/")
                    .addResourceLocations("classpath:/META-INF/resources/");
        } else {
            registry.addResourceHandler("quartz/index.html")
                    .addResourceLocations("classpath:/META-INF/resources/quartz/404.html");
        }
    }
}
