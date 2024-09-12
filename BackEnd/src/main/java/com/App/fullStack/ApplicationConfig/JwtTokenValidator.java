package com.App.fullStack.ApplicationConfig;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.App.fullStack.exception.FoundException;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.util.List;

public class JwtTokenValidator extends OncePerRequestFilter {

	@SuppressWarnings("null")
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws IOException, ServletException {
		String jwt = request.getHeader("Authorization");

		if (jwt != null && jwt.startsWith("Bearer ")) {
			jwt = jwt.substring(7); // Remove "Bearer " from the token
			System.out.println("JWT Token in JwtTokenValidator: " + jwt);

			try {
				SecretKey key = Keys.hmacShaKeyFor("wpembytrwcvnryxksdbqwjebruyGHyudqgwveytrtrCSnwifoesarjbwe".getBytes());

				Claims claims = Jwts.parser().verifyWith(key).build().parseSignedClaims(jwt).getPayload();

				String email = String.valueOf(claims.get("email"));
				String authorities = String.valueOf(claims.get("authorities"));

				List<GrantedAuthority> auth = AuthorityUtils.commaSeparatedStringToAuthorityList(authorities);

				Authentication authentication = new UsernamePasswordAuthenticationToken(email, null, auth);
				
				SecurityContextHolder.getContext().setAuthentication(authentication);

			} catch (Exception e) {
				throw new FoundException("Invalid token: " + e.getMessage());
			}
		}

		filterChain.doFilter(request, response);

	}
}