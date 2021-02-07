package com.myintroduce.config.jwt.filters;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.myintroduce.config.jwt.auth.PrincipalDetails;
import com.myintroduce.utill.JwtUtil;
import com.myintroduce.web.dto.login.LoginRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        ObjectMapper om = new ObjectMapper();
        LoginRequestDto loginRequestDto = null;

        try {
            loginRequestDto = om.readValue(request.getInputStream(), LoginRequestDto.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginRequestDto.getUsername(), loginRequestDto.getPassword());

        return authenticationManager.authenticate(authenticationToken);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        PrincipalDetails principalDetails = (PrincipalDetails) authResult.getPrincipal();
        String jwtToken = jwtUtil.createToken(principalDetails.getUser().getId(), principalDetails.getUsername());
        response.addHeader("Authentication", "Bearer "+jwtToken);
    }
}
