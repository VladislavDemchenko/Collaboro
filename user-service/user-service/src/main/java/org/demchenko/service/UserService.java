package org.demchenko.service;

import lombok.RequiredArgsConstructor;
import org.demchenko.entity.*;
import org.demchenko.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService {

    private static final String ROLE_USER = "ROLE_USER";
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public boolean existsByEmail(String email) {
        return true;
    }

    public User saveUser(User request) {
        return userRepository.save(request);
    }

    public Mono<UserResponse> createUser(UserRequest userRequest) {
        return userRepository.findByLogin(userRequest.login())
                .flatMap(existingUser -> Mono.<User>error(
                        new UserAlreadyExistsException("Username already exists: " + userRequest.login())))
                .switchIfEmpty(Mono.defer(() -> {
                    User newUser = new User();
                    newUser.setLogin(userRequest.login());
                    newUser.setPassword(passwordEncoder.encode(userRequest.password()));
                    newUser.setActive(true);
                    newUser.setRoles(Collections.singletonList(ROLE_USER));

                    return userRepository.save(newUser);
                })).doOnSuccess(user -> log.info("Created new user: {}", user.getUsername()))
                .doOnError(error -> log.error("Error creating user: {}", error.getMessage()));
    }

    public Mono<UserResponse> getUser(String login) {
        User user = userRepository.findByLogin(login);
        return new UserResponse(user.getLogin(), user.getPassword(), user.getRoles());
    }

}
