package org.demchenko.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.demchenko.entity.*;
import org.demchenko.exception.ResponseUserEmailAlreadyExistsException;
import org.demchenko.exception.ResponseUserLoginAlreadyExistsException;
import org.demchenko.exception.UserNotFoundException;
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

    public Mono<String> createUser(UserRequest request) {
        return throwUniqueLoginException(request)
                .switchIfEmpty(Mono.defer(() ->
                        throwUniqueEmailException(request)
                            .switchIfEmpty(Mono.defer(() ->
                                    createAndSaveUser(request)))))
                .onErrorResume(ResponseUserLoginAlreadyExistsException.class, ex -> //handle user login already exists exception
                        Mono.error(new ResponseStatusException(HttpStatus.CONFLICT, "Login already exists")))
                .onErrorResume(ResponseUserEmailAlreadyExistsException.class, ex -> //handle user email already exists exception
                        Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email already exists")))
                .doOnSuccess(user -> log.info("Created new user"))
                .doOnError(error -> log.error("Error creating user"));
    }

    public Mono<UserResponse> getUser(String login) {
        return getByLogin(login)
                .map(user -> new UserResponse(user.getLogin(), user.getPassword()))
                .switchIfEmpty(Mono.error(() ->{
                    throw new UserNotFoundException();
                }))
                .onErrorResume(UserNotFoundException.class, ex -> //handle user not found exception
                        Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")));
    }

    private Mono<String> createAndSaveUser(UserRequest request) {
        User newUser = new User();
        newUser.setLogin(request.login());
        newUser.setPassword(request.password());
        newUser.setEmail(request.email());
        newUser.setActive(true);
        newUser.setRoles(Collections.singletonList("USER"));
        return userRepository.save(newUser)
                .thenReturn("User created");
    }
    //todo: add caching
    private Mono<String> throwUniqueEmailException(UserRequest request) {
        return userRepository.findByEmail(request.email()) //throwing exception if user email already exists
                .flatMap(existingUser -> Mono.<String>defer(() -> {
                    throw new ResponseUserEmailAlreadyExistsException();
                }));
    }

    private Mono<String> throwUniqueLoginException(UserRequest request) {
        return getByLogin(request.login())
                .flatMap(existingUser -> Mono.<String>defer(() -> { //throwing exception if user login already exists
                    throw new ResponseUserLoginAlreadyExistsException();
                }));
    }
    //todo: add caching
    private Mono<User> getByLogin(String login) {
        return userRepository.findByLogin(login);
    }
}
