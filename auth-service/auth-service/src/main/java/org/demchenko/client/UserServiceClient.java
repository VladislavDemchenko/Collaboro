package org.demchenko.client;

import org.demchenko.entity.UserAuthorizationRequest;
import org.demchenko.entity.UserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import reactor.core.publisher.Mono;

@Component
@FeignClient(name = "user-service")
public interface UserServiceClient {

    @PostMapping("/users/register")
    Mono<UserResponse> registerUser(@RequestBody UserAuthorizationRequest authorization);

    @PostMapping("/users/getOne")
    Mono<UserResponse> getUser(@RequestParam String login);
}
