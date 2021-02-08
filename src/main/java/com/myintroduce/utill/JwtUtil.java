package com.myintroduce.utill;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Date;

public class JwtUtil {

    private Key key;

    public JwtUtil(String signature) {
        this.key = Keys.hmacShaKeyFor(signature.getBytes());
    }

    public String createToken(long id, String username) {
        long expiredTime = 1000 * 60L * 60L * 2L; // 2시간 설정
        Date extDate = new Date();
        extDate.setTime(extDate.getTime() + expiredTime);

        return Jwts.builder()
                .claim("id", id)
                .claim("username", username)
                .setExpiration(extDate)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public Boolean isExpired(String token) {
        Claims claims = getClaims(token);
        Date expDate = claims.getExpiration();
        return expDate.before(new Date());
    }
}
