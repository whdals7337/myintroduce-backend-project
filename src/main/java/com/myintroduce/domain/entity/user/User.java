package com.myintroduce.domain.entity.user;

import lombok.*;
import javax.persistence.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;
    private String username;
    private String password;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    @Builder
    public User(Long id, String password,
                String username, UserRole role) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;
    }
}
