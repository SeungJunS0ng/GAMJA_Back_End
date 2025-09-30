package com.study.board.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 웹 MVC 설정 클래스
 * CORS, 정적 리소스 핸들링 등을 설정합니다.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * CORS 설정
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "HEAD", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }

    /**
     * 정적 리소스 핸들러 설정
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 업로드된 파일에 대한 접근 경로 설정
        registry.addResourceHandler("/files/**")
                .addResourceLocations("file:" + System.getProperty("user.dir") + "/src/main/resources/static/files/")
                .setCachePeriod(3600);
    }
}
