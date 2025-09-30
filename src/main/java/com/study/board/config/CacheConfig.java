package com.study.board.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 캐시 설정 클래스
 * 애플리케이션의 캐싱 전략을 정의합니다.
 */
@Configuration
@EnableCaching
public class CacheConfig {

    /**
     * 캐시 매니저 설정
     * 메모리 기반 캐시를 사용합니다.
     */
    @Bean
    public CacheManager cacheManager() {
        ConcurrentMapCacheManager cacheManager = new ConcurrentMapCacheManager();
        cacheManager.setCacheNames("boards", "boardList", "popularBoards");
        return cacheManager;
    }
}
