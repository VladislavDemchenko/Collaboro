package org.demchenko.entity;

import lombok.*;
import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    private String id;

    @Indexed(unique = true)
    private String login;

    private String password;

    @Indexed(unique = true)
    private String email;

    private List<String> roles;

    private boolean active;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public User(String login, String password, boolean b, List<String> user) {
        this.login = login;
        this.password = password;
        this.active = b;
        this.roles = (List<String>) user;
    }
}
