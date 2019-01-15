//package com.beelego;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.cors.CorsConfiguration;
//import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
//import org.springframework.web.filter.CorsFilter;
//
///**
// * @Author: rns
// * @Date: 2019/1/13 下午8:40
// * @Description: CrosConfiguration
// */
//@Configuration
//public class CrosConfiguration {
//
//  private CorsConfiguration buildConfig() {
//    CorsConfiguration corsConfiguration = new CorsConfiguration();
//    corsConfiguration.addAllowedOrigin("*"); // 允许任何域名使用
//    corsConfiguration.addAllowedHeader("*"); // 允许任何头
//    corsConfiguration.addAllowedMethod("*"); // 允许任何方法（post、get等）
//    return corsConfiguration;
//  }
//
//  @Bean
//  public CorsFilter corsFilter() {
//    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//    source.registerCorsConfiguration("/**", buildConfig()); // 对接口配置跨域设置
//    return new CorsFilter(source);
//  }
//}
