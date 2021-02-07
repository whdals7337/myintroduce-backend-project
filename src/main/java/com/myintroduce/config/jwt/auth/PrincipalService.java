package com.myintroduce.config.jwt.auth;

import com.myintroduce.domain.entity.user.User;
import com.myintroduce.error.exception.session.LoginInfoWrongException;
import com.myintroduce.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PrincipalService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username).orElseThrow(LoginInfoWrongException::new);
        return new PrincipalDetails(user);
    }
}
