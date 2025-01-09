package org.demchenko.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.demchenko.entity.*;
import org.demchenko.exception.UserAlreadyExistsException;
import org.demchenko.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Collections;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    public Mono<UserResponse> createUser(UserRequest request) {
        return userRepository.findByLogin(request.login())
                .flatMap(existingUser -> Mono.<UserResponse>error(
                        new UserAlreadyExistsException(HttpStatus.CONFLICT, "Username already exists: " + request.login())))
                .switchIfEmpty(Mono.defer(() -> {

                    User newUser = new User();
                    newUser.setLogin(request.login());
                    newUser.setPassword(passwordEncoder.encode(request.login()));
                    newUser.setActive(true);
                    newUser.setRoles(Collections.singletonList("USER"));
                    return userRepository.save(newUser)
                            .map(user -> new UserResponse(user.getLogin(), user.getPassword()));
                }));
//                .doOnSuccess(user -> log.info("Created new user: {}", user.getLogin()))
//                .doOnError(error -> log.error("Error creating user: {}", error.getMessage()));
    }

    public Mono<UserResponse> getUser(String login) {
        User user = userRepository.findByLogin(login);
        return new UserResponse(user.getLogin(), user.getPassword(), user.getRoles());
    }

}
