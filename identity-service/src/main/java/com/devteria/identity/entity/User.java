package com.devteria.identity.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @Column(
            name = "username",
            unique = true,
            columnDefinition = "VARCHAR(255) COLLATE utf8mb4_unicode_ci") // charset = utf8mb4_unicode_ci
            // Với ci = case-insensitive. Không phân biệt chữ hoa với chữ thường (ThAi == thai)
    String username;

    @Column(name = "email", unique = true, columnDefinition = "VARCHAR(255) COLLATE utf8mb4_unicode_ci")
    String email;

    String password;

    @ManyToMany
    Set<Role> roles;
}
