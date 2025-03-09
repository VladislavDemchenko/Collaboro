package org.demchenko.security;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.demchenko.entity.ApiError;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter implements WebFilter {

	private final JwtAuthenticationManager jwtAuthenticationManager;

	private boolean shouldSkip(String path) {
		if (path == null) {
			return false;
		}
		return path.contains("/api/auth/register") || path.contains("/api/auth/login");
	}

	/**
	 * Filters incoming requests for JWT authentication.
	 * 
	 * @param exchange the current server web exchange
	 * @param chain    the web filter chain
	 * @return a {@link Mono<Void>} indicating when request processing is complete
	 */
	@Override
	public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
	    String path = exchange.getRequest().getURI().getPath();
	    if (shouldSkip(path)) {
	        return chain.filter(exchange);
	    }

	    String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
	    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
	        return handleAuthenticationError(exchange, "No valid authorization token found");
	    }

	    String token = authHeader.substring(7);
	    return jwtAuthenticationManager.authenticate(
	            JwtAuthenticationToken.builder()
	                .token(token)
	                .build()
	        )
	        .flatMap(authentication -> {
	            authentication.setAuthenticated(true);
	            return chain.filter(exchange)
	                .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication));
	        })
	        .onErrorResume(AuthenticationException.class, 
	            e -> handleAuthenticationError(exchange, e.getMessage()));
	}

	private Mono<Void> handleAuthenticationError(ServerWebExchange exchange, String message) {
	    exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
	    exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
	    
	    ApiError error = ApiError.builder()
	        .timestamp(LocalDateTime.now().toString())
	        .status(HttpStatus.UNAUTHORIZED.value())
	        .error("Authentication Failed")
	        .message(message)
	        .path(exchange.getRequest().getPath().value())
	        .traceId(UUID.randomUUID().toString())
	        .build();

	    byte[] bytes;
	    try {
	        bytes = new ObjectMapper().writeValueAsBytes(error);
	    } catch (JsonProcessingException e) {
	        return exchange.getResponse().setComplete();
	    }

	    DataBuffer buffer = exchange.getResponse()
	        .bufferFactory()
	        .wrap(bytes);
	        
	    return exchange.getResponse().writeWith(Mono.just(buffer));
	}
}