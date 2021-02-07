package com.myintroduce.config.jwt.filters;

import com.myintroduce.config.jwt.auth.PrincipalDetails;
import com.myintroduce.domain.entity.user.User;
import com.myintroduce.error.exception.session.LoginInfoWrongException;
import com.myintroduce.repository.user.UserRepository;
import com.myintroduce.utill.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JwtAuthorizationFilter extends BasicAuthenticationFilter {

    private JwtUtil jwtUtil;
    private UserRepository userRepository;

    public JwtAuthorizationFilter(AuthenticationManager authenticationManager, JwtUtil jwtUtil, UserRepository userRepository) {
        super(authenticationManager);
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        String header = request.getHeader("Authorization");
        if(header == null || !header.startsWith("Bearer")) {
            chain.doFilter(request,response);
            return;
        }

        String token = header.substring("Bearer ".length());
        Claims claims = jwtUtil.getClaims(token);

        if (claims != null && !jwtUtil.isExpired(token)) {
            User user = userRepository.findByUsername(String.valueOf(claims.get("username"))).orElseThrow(LoginInfoWrongException::new);
            PrincipalDetails principalDetails = new PrincipalDetails(user);

            Authentication authentication = new UsernamePasswordAuthenticationToken(principalDetails, null, principalDetails.getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        chain.doFilter(request, response);
    }
}
