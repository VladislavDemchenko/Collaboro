package org.demchenko.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.demchenko.client.UserServiceClient;
import org.demchenko.entity.UserAuthenticationRequest;
import org.demchenko.entity.UserAuthorizationRequest;
import org.demchenko.entity.UserResponse;
import org.demchenko.exception.InvalidPasswordException;
import org.demchenko.exception.UserLoginAlreadyExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserServiceClient userServiceClient;

    @Autowired
    private final BCryptPasswordEncoder passwordEncoder;

    public Mono<String> register(Mono<UserAuthorizationRequest> request) {
        return request
                .doOnNext( userAuthRequest ->
                        userAuthRequest.setPassword(passwordEncoder.encode(userAuthRequest.getPassword())))
                .flatMap(userServiceClient::registerUser);
    }

    public Mono<UserResponse> authenticate(Mono<UserAuthenticationRequest> userAuthenticationRequest) {
        return userAuthenticationRequest
                .flatMap(uAR ->{
                    return userServiceClient.getUser(uAR)
                            .flatMap(userResponse -> {
                                if (!passwordEncoder.matches(uAR.getPassword(), userResponse.getPassword())) {
                                    throw new InvalidPasswordException();
                                }
                                return Mono.just(userResponse);
                            });
                });
    }
}
