package com.App.fullStack.ApplicationConfig;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.Authentication; 

import javax.crypto.SecretKey; 
import java.util.Date;

public class JwtProvider {
	static SecretKey key = Keys.hmacShaKeyFor("wpembytrwcvnryxksdbqwjebruyGHyudqgwveytrtrCSnwifoesarjbwe".getBytes());

	public static String generateToken(Authentication auth) {
		String jwt = Jwts.builder()
				.issuedAt(new Date())
				.expiration(new Date(new Date().getTime()+10800 * 1000))
				.claim("email", auth.getName())
				.claim("authorities", auth.getAuthorities())
				.signWith(key)
				.compact();
		System.out.println("Token for parsing in JwtProvider: " + jwt);
		return jwt;
	}
} 
