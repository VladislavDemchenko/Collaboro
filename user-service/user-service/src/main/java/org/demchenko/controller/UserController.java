package org.demchenko.controller;

import lombok.RequiredArgsConstructor;
import org.demchenko.entity.UserRequest;
import org.demchenko.entity.UserResponse;
import org.demchenko.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public Mono<UserResponse> registerUser(@RequestBody UserRequest request) {
        return userService.createUser(request);
    }
    @GetMapping("/getOne")
    public Mono<UserResponse> getUser(@RequestParam String login) {
        return userService.getUser(login);
    }
}
