package com.study.board.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("감자 게시판 API")
                        .description("Spring Boot로 구현한 게시판 시스템 API 문서")
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("GAMJA Team")
                                .email("contact@gamja.com")
                                .url("https://github.com/SeungJunS0ng/GAMJA_Back_End"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")));
    }
}
