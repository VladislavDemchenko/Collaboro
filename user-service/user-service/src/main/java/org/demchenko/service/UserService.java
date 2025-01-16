package org.demchenko.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.demchenko.entity.*;
import org.demchenko.exception.ResponseUserAlreadyExistsException;
import org.demchenko.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.util.Collections;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    @Autowired
    private final UserRepository userRepository;



    public Mono<UserResponse> createUser(UserRequest request) {
        return userRepository.findByLogin(request.login())
                .flatMap(existingUser -> Mono.<UserResponse>defer(() -> { //throwing exception if user already exists
                    throw new ResponseUserAlreadyExistsException();
                }))
                .switchIfEmpty(Mono.defer(() -> {
                    User newUser = new User();
                    newUser.setLogin(request.login());
                    newUser.setPassword(request.password());
                    newUser.setActive(true);
                    newUser.setRoles(Collections.singletonList("USER"));
                    return userRepository.save(newUser)
                            .map(user -> new UserResponse(user.getLogin(), user.getPassword()));
                }))
                .onErrorResume(ResponseUserAlreadyExistsException.class, ex -> //handle user already exists exception
                        Mono.error(new ResponseStatusException(HttpStatus.CONFLICT, ex.getMessage()))
                )
                .doOnSuccess(user -> log.info("Created new user: {}", user.getLogin()))
                .doOnError(error -> log.error("Error creating user: {}", error.getMessage()));
    }


    public Mono<UserResponse> getUser(String login) {
        return userRepository.findByLogin(login)
                .map(user -> new UserResponse(user.getLogin(), user.getPassword()));
//                .switchIfEmpty(Mono.error(new UserNotFoundException(HttpStatus.NOT_FOUND, "User not found")));
    }

}
