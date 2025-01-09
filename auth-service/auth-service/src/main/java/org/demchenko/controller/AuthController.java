package org.demchenko.controller;

import lombok.RequiredArgsConstructor;
import org.demchenko.entity.AuthResponse;
import org.demchenko.entity.UserAuthenticationRequest;
import org.demchenko.entity.UserAuthorizationRequest;
import org.demchenko.security.JwtService;
import org.demchenko.service.AuthService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@RequestMapping("/api/auth")
@RestController
@RequiredArgsConstructor
public class AuthController {

    private final JwtService jwtService;

    private final AuthService authService;

//    @PostMapping("/register")
//    public Mono<AuthResponse> register(@RequestBody UserAuthorizationRequest authorization) {
//        return authService.registerUser(authorization)
//                .map(userResponse -> AuthResponse.builder()
//                .token(jwtService.generateToken(userResponse.login()))
//                .username(userResponse.login())
//                .build());
//    }

    //todo: think about cast response to AuthResponse
    @PostMapping("/users/register")
    public Mono<ServerResponse> registerUser(ServerRequest request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    public Mono<AuthResponse> login(@RequestBody UserAuthenticationRequest userAuthenticationRequest) {
        return authService.authenticate(userAuthenticationRequest)
                .map(userResponse -> AuthResponse.builder()
                .token(jwtService.generateToken(userResponse.login()))
                .username(userResponse.login())
                .build());
    }


}
