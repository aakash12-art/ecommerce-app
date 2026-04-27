package com.ecommerce.security;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.ecommerce.entity.Role;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

/** Creates and validates JWT access tokens (HS256). */
@Component
public class JwtTokenProvider {

	private final SecretKey key;
	private final long expirationMs;

	public JwtTokenProvider(
			@Value("${app.jwt.secret}") String secret,
			@Value("${app.jwt.expiration-ms}") long expirationMs) {
		// Key must be long enough for HS256 (>= 256 bits).
		this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
		this.expirationMs = expirationMs;
	}

	public String createToken(String email, Role role) {
		Date now = new Date();
		Date expiry = new Date(now.getTime() + expirationMs);
		return Jwts.builder()
				.subject(email)
				.claim("role", role.name())
				.issuedAt(now)
				.expiration(expiry)
				.signWith(key)
				.compact();
	}

	public boolean validateToken(String token) {
		try {
			Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
			return true;
		}
		catch (JwtException | IllegalArgumentException e) {
			return false;
		}
	}

	public String getEmail(String token) {
		return parseClaims(token).getSubject();
	}

	public Role getRole(String token) {
		String r = parseClaims(token).get("role", String.class);
		return r != null ? Role.valueOf(r) : Role.USER;
	}

	private Claims parseClaims(String token) {
		return Jwts.parser().verifyWith(key).build()
				.parseSignedClaims(token)
				.getPayload();
	}
}
