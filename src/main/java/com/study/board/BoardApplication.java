package com.study.board;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Spring Boot 메인 애플리케이션 클래스
 *
 * - API 문서: http://localhost:8080/swagger-ui.html
 * - H2 Console: http://localhost:8080/h2-console
 * - Actuator: http://localhost:8080/actuator
 */
@SpringBootApplication
@EnableJpaAuditing
public class BoardApplication {

	public static void main(String[] args) {
		SpringApplication.run(BoardApplication.class, args);
	}

}
