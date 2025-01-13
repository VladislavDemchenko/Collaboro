package org.demchenko.entity;

import lombok.*;

@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private String login;
//    private String postalCode;
//    private boolean active;
//    private String role;
}
