package com.exa.jwt;

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;

public class CreateJwt {
    public static void main(String[] args) {
        /**
         * setIssuedAt(new Date())登陆时间
         * signWith(签名加密方式，盐)
         * setExpiration(过期时间)
         * claim(自定义键值对)
         */
        JwtBuilder jwtBuilder=Jwts.builder()
                .setId("666")
                .setSubject("用户名")
                .setIssuedAt(new Date())
                .signWith(SignatureAlgorithm.HS256,"test")
                .setExpiration(new Date(new Date().getTime()+60*60))
                .claim("role","admin");

        System.out.println(jwtBuilder.compact());
    }
}
