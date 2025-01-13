package org.demchenko.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

/**
 * This class configures Spring Security for the weather application.
 *
 */
@Configuration
@EnableWebFluxSecurity
@RequiredArgsConstructor
public class SecurityConfig {
	

	private final JwtAuthenticationFilter jwtAuthenticationFilter;

	/*
	 * Creates a {@link SecurityWebFilterChain} bean to configure Spring Security.
	 */
	@Bean
	public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
	    return http
	        .authorizeExchange(exchanges -> exchanges
	            .pathMatchers( // path that does not require authentication
						"/api/auth/login", "/api/auth/register", "/public/**"
	            ).permitAll()
	            .anyExchange().authenticated()
	        )
	        .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
	        .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
			.csrf(ServerHttpSecurity.CsrfSpec::disable)
			.addFilterAt(jwtAuthenticationFilter, SecurityWebFiltersOrder.AUTHENTICATION)
	        .build();
	}

}