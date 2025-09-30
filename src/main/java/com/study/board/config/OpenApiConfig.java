package com.study.board.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI(Swagger) 설정 클래스
 * API 문서화를 위한 설정을 제공합니다.
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("게시판 API")
                        .version("1.0.0")
                        .description("Spring Boot를 사용한 게시판 애플리케이션 API 문서")
                        .contact(new Contact()
                                .name("개발팀")
                                .email("dev@example.com")
                                .url("https://github.com/SeungJunS0ng/GAMJA_Back_End"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")));
    }
}
