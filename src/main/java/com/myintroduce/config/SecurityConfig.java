package com.myintroduce.config;

import com.myintroduce.config.jwt.filters.JwtAuthenticationFilter;
import com.myintroduce.config.jwt.filters.JwtAuthorizationFilter;
import com.myintroduce.repository.user.UserRepository;
import com.myintroduce.utill.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@RequiredArgsConstructor
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Value("${jwt.signature}")
    private String signature;

    private final UserRepository userRepository;
    private final CorsConfig corsConfig;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JwtUtil jwtUtil() {
        return new JwtUtil(signature);
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/css/**", "/js/**", "/img/**", "/images/**", "/lib/**"
                ,"/v2/api-docs", "/swagger-resources/**", "/swagger-ui.html", "/webjars/**", "/swagger/**");

    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .addFilter(corsConfig.corsFilter())
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .formLogin().disable()
                .httpBasic().disable()

                .addFilter(new JwtAuthenticationFilter(authenticationManager(), jwtUtil()))
                .addFilter(new JwtAuthorizationFilter(authenticationManager(), jwtUtil(), userRepository))
                .authorizeRequests()
                .antMatchers(HttpMethod.GET,"/api/**").permitAll()
                .antMatchers("/profile").permitAll()
                .anyRequest().authenticated();
    }
}
