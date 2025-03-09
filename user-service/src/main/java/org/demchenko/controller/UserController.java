package org.demchenko.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.demchenko.entity.UserRequest;
import org.demchenko.entity.UserResponse;
import org.demchenko.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public Mono<String> registerUser(@RequestBody UserRequest request) {
        return userService.createUser(request);
    }
    @PostMapping("/getOne")
    public Mono<UserResponse> loginUser(@RequestBody String login) {
        return userService.getUser(login);
    }
}
