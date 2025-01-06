package org.demchenko.service;

import lombok.RequiredArgsConstructor;
import org.demchenko.client.UserServiceClient;
import org.demchenko.entity.UserAuthenticationRequest;
import org.demchenko.entity.UserAuthorizationRequest;
import org.demchenko.entity.UserResponse;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserServiceClient userServiceClient;

    private final BCryptPasswordEncoder passwordEncoder;

    public Mono<UserResponse> register(UserAuthorizationRequest authorization) {
        passwordEncoder.encode(authorization.password());
        return userServiceClient.registerUser(authorization);
    }

    public Mono<UserResponse> authenticate(UserAuthenticationRequest userAuthenticationRequest) {
        return userServiceClient.getUser(userAuthenticationRequest.login())
            .flatMap(existingUser -> Mono.<UserResponse>error(
                    new UserAlreadyExistsException("Username already exists: " + userAuthenticationRequest.login())))
            .filter(user -> passwordEncoder
                    .matches(userAuthenticationRequest.password(), user.password()))
            .switchIfEmpty(Mono.error(new RuntimeException("Invalid credentials")));

    }
}
