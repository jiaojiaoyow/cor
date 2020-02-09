package com.exa.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

public class parseJwt {
    public static void main(String[] args) {
        Claims claims=Jwts.parser()
                .setSigningKey("test")
                .parseClaimsJws("eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiI2NjYiLCJzdWIiOiLnlKjmiLflkI0iLCJpYXQiOjE1ODExODM0NTF9.ZLzbJV2Msnt0oMfpC7wqGXg3hSPn-TrC9myTIYfWu5M")
                .getBody();

        System.out.println("用户id："+claims.getId());
        System.out.println("用户名："+claims.getSubject());
    }
}
