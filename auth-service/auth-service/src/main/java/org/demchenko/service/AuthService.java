package org.demchenko.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.demchenko.client.UserServiceClient;
import org.demchenko.entity.UserAuthenticationRequest;
import org.demchenko.entity.UserAuthorizationRequest;
import org.demchenko.entity.UserResponse;
import org.demchenko.exception.UserAlreadyExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserServiceClient userServiceClient;

    @Autowired
    private final BCryptPasswordEncoder passwordEncoder;

    public Mono<ServerResponse> register(ServerRequest request) {
        return request.bodyToMono(UserAuthorizationRequest.class)
                .doOnNext( userAuthRequest ->
                        userAuthRequest.setPassword(passwordEncoder.encode(userAuthRequest.getPassword())))
                .flatMap(userServiceClient::registerUser)
                .flatMap(userResponse ->
                        ServerResponse.ok().bodyValue("Successfully authenticated")); // success response
    }

    public Mono<UserResponse> authenticate(UserAuthenticationRequest userAuthenticationRequest) {
        return userServiceClient.getUser(userAuthenticationRequest.getLogin())
                .flatMap(existingUser -> Mono.<UserResponse>error(
                        new UserAlreadyExistsException(HttpStatus.BAD_REQUEST, "Username already exists: " + userAuthenticationRequest.getLogin())))
                .filter(user -> passwordEncoder
                        .matches(userAuthenticationRequest.getPassword(), user.getPassword()))
                .switchIfEmpty(Mono.error(new RuntimeException("Invalid credentials")));

    }
}
