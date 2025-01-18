package org.demchenko.client;

import org.demchenko.entity.UserAuthenticationRequest;
import org.demchenko.entity.UserAuthorizationRequest;
import org.demchenko.entity.UserResponse;
import org.demchenko.exception.*;
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

    public Mono<String> registerUser(UserAuthorizationRequest authorization) {
        return webClient.post()
                .uri("/users/register")
                .bodyValue(authorization)
                .retrieve()
                .onStatus(hs -> hs.isSameCodeAs(HttpStatus.CONFLICT), response ->
                        response.bodyToMono(String.class)
                                .flatMap(_ -> Mono.error(new UserLoginAlreadyExistsException())))
                .onStatus(hs -> hs.isSameCodeAs(HttpStatus.BAD_REQUEST), response ->
                        response.bodyToMono(String.class)
                                .flatMap(_ -> Mono.error(new UserEmailAlreadyExistsException())))
                .onStatus(HttpStatusCode::is4xxClientError, response -> //cast all 4xx exceptions to UserAlreadyExistsException
                        response.bodyToMono(String.class) //todo: crate global exception
                                .flatMap(_ -> Mono.error(new Unexpected4XXException())))
                .onStatus(HttpStatusCode::is5xxServerError, response -> //cast all 5xx exceptions to RuntimeException
                        response.bodyToMono(String.class)
                                .flatMap(_ -> Mono.error(new Unexpected5XXException())))
                .bodyToMono(String.class);
    }

    public Mono<UserResponse> getUser(UserAuthenticationRequest request) {
        return webClient.post()
                .uri("/users/getOne")
                .bodyValue(request.getLogin())
                .retrieve()
                .onStatus(hs -> hs.isSameCodeAs(HttpStatus.NOT_FOUND), response ->
                        response.bodyToMono(String.class)
                                .flatMap(_ -> Mono.error(new NotFoundUserException())))
                .onStatus(HttpStatusCode::is4xxClientError, response -> //cast all 4xx exceptions to UserAlreadyExistsException
                        response.bodyToMono(String.class) //todo: crate global exception
                                .flatMap(_ -> Mono.error(new Unexpected4XXException())))
                .onStatus(HttpStatusCode::is5xxServerError, response -> //cast all 5xx exceptions to RuntimeException
                        response.bodyToMono(String.class)
                                .flatMap(_ -> Mono.error(new Unexpected5XXException())))
                .bodyToMono(UserResponse.class);
    }
}
