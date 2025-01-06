package org.demchenko.entity;

public record UserAuthorizationRequest(String login, String password, String email) {
}
