package org.demchenko.entity;

import java.util.Set;

public record UserResponse(String login, String password, Set<Role> roles) {
}
