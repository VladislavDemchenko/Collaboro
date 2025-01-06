package org.demchenko.repository;


import org.demchenko.entity.User;
import org.demchenko.entity.UserResponse;
import org.springframework.data.mongodb.repository.MongoRepository;
import reactor.core.publisher.Mono;

public interface UserRepository extends MongoRepository<User, String> {
    Mono<User> findByLogin(String login);
}
