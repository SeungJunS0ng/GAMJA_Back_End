package com.study.board.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 보안 설정 클래스
 * 현재는 기본적인 보안 설정만 적용
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable() // API 테스트를 위해 CSRF 비활성화
            .authorizeRequests()
                .antMatchers("/", "/board/**", "/api/**", "/h2-console/**", "/files/**").permitAll()
                .anyRequest().authenticated()
            .and()
            .headers().frameOptions().disable(); // H2 Console 접근을 위해 설정
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
