package org.demchenko.client;

import org.demchenko.entity.UserAuthorizationRequest;
import org.demchenko.entity.UserResponse;
import org.demchenko.exception.UserAlreadyExistsException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class UserServiceClient {

    private final WebClient webClient;

    public UserServiceClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://localhost:8081").build();
    }

    public Mono<UserResponse> registerUser(UserAuthorizationRequest authorization) {
        return webClient.post()
                .uri("/users/register")
                .bodyValue(authorization)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, response ->
                        response.bodyToMono(String.class)
                                .flatMap(errorMessage -> Mono.error(new UserAlreadyExistsException(HttpStatus.BAD_REQUEST, errorMessage))))
                .onStatus(HttpStatusCode::is4xxClientError, response ->
                        response.bodyToMono(String.class)
                                .flatMap(errorMessage -> Mono.error(new RuntimeException("Server error: " + errorMessage))))
                .bodyToMono(UserResponse.class);
    }

    public Mono<UserResponse> getUser(String login) {
        return webClient.post()
                .uri(uriBuilder -> uriBuilder.path("/users/getOne").queryParam("login", login).build())
                .retrieve()
                .bodyToMono(UserResponse.class);
    }
}
