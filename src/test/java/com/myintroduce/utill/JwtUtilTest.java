package com.myintroduce.utill;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JwtUtilTest {

    private String key = "wellbell_dev_jwt_signature_value";
    private JwtUtil jwtUtil = new JwtUtil(key);

    @Test
    void createToken_test() {
        // given

        // when
        String token = jwtUtil.createToken(1004L, "testerName");

        // then
        assertThat(token).contains(".");
    }

    @Test
    void getClaims_test() {
        // given
        String token = jwtUtil.createToken(1004L, "testerName");

        // when
        Claims claims = jwtUtil.getClaims(token);

        // then
        assertThat(claims.get("id", Long.class)).isEqualTo(1004L);
        assertThat(claims.get("username")).isEqualTo("testerName");
    }
    
    @Test
    void isExpired_test() {
        // given
        String token = jwtUtil.createToken(1004L, "testerName");

        // when
        Boolean expired = jwtUtil.isExpired(token);

        // then
        assertThat(expired).isFalse();
    }
}