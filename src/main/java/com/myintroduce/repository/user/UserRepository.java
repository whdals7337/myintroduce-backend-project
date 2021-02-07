package com.myintroduce.repository.user;

import com.myintroduce.domain.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // 아이디로 User 정보 조회
    Optional<User> findByUsername(String username);
}
