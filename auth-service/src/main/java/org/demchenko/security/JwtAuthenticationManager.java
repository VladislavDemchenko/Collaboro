package org.demchenko.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;


@Component
@Primary
@RequiredArgsConstructor
public class JwtAuthenticationManager implements ReactiveAuthenticationManager {
	
    private static final String ROLE_USER = "ROLE_USER";
	private final JwtService jwtService;

	/**
	 * Authenticates the provided {@link Authentication} object.
	 *
	 * <p>This method returns a {@link Mono} that emits the newly created
	 * {@link JwtAuthenticationToken} if the authentication is successful,
	 * or an empty {@link Mono} otherwise.</p>
	 *
	 * @param authentication The authentication object to authenticate.
	 * @return A {@link Mono} that emits the authenticated {@link JwtAuthenticationToken}
	 * if successful, or an empty {@link Mono} otherwise.
	 */
    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        return Mono.justOrEmpty(authentication)
            .cast(JwtAuthenticationToken.class)
            .flatMap(auth -> jwtService.validateTokenAndGetUsername(auth.getToken())
                .map(username -> {
                    List<SimpleGrantedAuthority> authorities = 
                        Collections.singletonList(new SimpleGrantedAuthority(ROLE_USER));
                    return JwtAuthenticationToken.builder()
                    		.token(auth.getToken())
                    		.username(username)
                    		.authorities(authorities)
                    		.build();
                })
            );
    }
}