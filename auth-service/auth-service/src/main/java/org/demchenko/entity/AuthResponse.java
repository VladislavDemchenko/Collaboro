package org.demchenko.entity;

import lombok.Builder;

@Builder
public record AuthResponse(String token, String username) {
}
