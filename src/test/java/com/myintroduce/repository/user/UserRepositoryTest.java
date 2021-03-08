package com.myintroduce.repository.user;

import com.myintroduce.domain.entity.user.User;
import com.myintroduce.domain.entity.user.UserRole;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Transactional
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    public void findByUsername_test() throws Exception {
        // given
        User user = User.builder()
                .username("tester")
                .password("pass")
                .role(UserRole.ADMIN)
                .build();
        userRepository.save(user);

        // when
        Optional<User> findUser = userRepository.findByUsername("tester");

        // then
        assertThat(findUser.isPresent()).isTrue();
        assertThat(findUser.get()).isEqualTo(user);
        assertThat(findUser.get().getId()).isNotNull();
    }
}