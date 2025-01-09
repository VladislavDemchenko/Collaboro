package org.demchenko.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.demchenko.client.UserServiceClient;
import org.demchenko.entity.UserAuthenticationRequest;
import org.demchenko.entity.UserAuthorizationRequest;
import org.demchenko.entity.UserResponse;
import org.demchenko.exception.UserAlreadyExistsException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserServiceClient userServiceClient;

    private final BCryptPasswordEncoder passwordEncoder;

    public Mono<ServerResponse> register(ServerRequest request) {
        return request.bodyToMono(UserAuthorizationRequest.class)
                .flatMap(userServiceClient::registerUser)
                .flatMap(userResponse -> ServerResponse.ok().bodyValue(userResponse)) // Успішна відповідь
                .onErrorResume(UserAlreadyExistsException.class, ex ->
                        ServerResponse.status(HttpStatus.CONFLICT).bodyValue(ex.getMessage())) // Обробка помилки
                .onErrorResume(error ->
                        ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
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
