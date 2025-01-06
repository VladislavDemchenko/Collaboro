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
    public Mono<ResponseEntity<UserResponse>> registerUser(@RequestBody UserRequest request) {
        // Перевірка на унікальність email
//        if (userService.existsByEmail(request.email())) {
//            throw new RuntimeException("Email already exists!");
//        }

//        userService.saveUser(request);
        return ResponseEntity.ok(userService.createUser(request));
    }
    @GetMapping("/getOne")
    public Mono<ResponseEntity<UserResponse>> getUser(@RequestParam String login) {
        return ResponseEntity.ok(userService.getUser(login));
    }
}