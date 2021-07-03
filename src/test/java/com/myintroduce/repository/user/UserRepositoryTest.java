package com.myintroduce.repository.user;

import com.myintroduce.domain.entity.user.User;
import com.myintroduce.domain.entity.user.UserRole;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Transactional
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void findByUsername_test() {
        // given
        User user = User.builder()
                .username("tester")
                .password("pass")
                .role(UserRole.ADMIN)
                .build();
        userRepository.save(user);

        // when
        User findUser = userRepository.findByUsername("tester").orElse(null);

        // then
        assert findUser != null;
        assertThat(findUser).isEqualTo(user);
        assertThat(findUser.getId()).isNotNull();
    }
}