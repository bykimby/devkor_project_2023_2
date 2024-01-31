package com.example.devkorproject.common.config;

import com.example.devkorproject.auth.jwt.*;


import jakarta.servlet.DispatcherType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig{
    // JWT 필터 및 기타 구성요소 주입
    private final JwtUtil jwtUtil;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

    private CustomAuthenticationProvider customAuthenticationProvider;
    private CustomUserDetailService customUserDetailService;


    public SecurityConfig(JwtUtil jwtUtil, JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint, JwtAccessDeniedHandler jwtAccessDeniedHandler, CustomAuthenticationProvider customAuthenticationProvider, CustomUserDetailService customUserDetailService) {
        this.jwtUtil = jwtUtil;
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
        this.jwtAccessDeniedHandler = jwtAccessDeniedHandler;
        this.customAuthenticationProvider = customAuthenticationProvider;
        this.customUserDetailService = customUserDetailService;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())       // CSRF 보호 비활성화
                .sessionManagement(sessionManagement ->
                        sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .exceptionHandling(exceptionHandling ->
                        exceptionHandling
                                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                                .accessDeniedHandler(jwtAccessDeniedHandler))
                .authorizeRequests(authRequests ->
                        authRequests
                                .dispatcherTypeMatchers(DispatcherType.FORWARD).permitAll()
                                .requestMatchers("/auth/**").permitAll()
                                .requestMatchers("/customer/login").permitAll()
                                .requestMatchers("/**").hasAuthority("ROLE_USER")
                                .anyRequest().authenticated())
                .userDetailsService(customUserDetailService)
                .apply(new JwtSecurityConfig(jwtUtil));
        return http.build();
    }

    @Autowired
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(customAuthenticationProvider);
    }
}
