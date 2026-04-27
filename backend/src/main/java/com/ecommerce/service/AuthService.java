package com.ecommerce.service;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ecommerce.dto.AuthResponse;
import com.ecommerce.dto.LoginRequest;
import com.ecommerce.dto.RegisterRequest;
import com.ecommerce.dto.UserDto;
import com.ecommerce.entity.Role;
import com.ecommerce.entity.User;
import com.ecommerce.exception.BadRequestException;
import com.ecommerce.repository.UserRepository;
import com.ecommerce.security.JwtTokenProvider;

@Service
public class AuthService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtTokenProvider jwtTokenProvider;

	public AuthService(
			UserRepository userRepository,
			PasswordEncoder passwordEncoder,
			JwtTokenProvider jwtTokenProvider) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.jwtTokenProvider = jwtTokenProvider;
	}

	@Transactional
	public AuthResponse register(RegisterRequest req) {
		if (userRepository.existsByEmail(req.getEmail())) {
			throw new BadRequestException("Email already registered");
		}
		User user = new User();
		user.setName(req.getName());
		user.setEmail(req.getEmail());
		user.setPassword(passwordEncoder.encode(req.getPassword()));
		user.setRole(Role.USER);
		user = userRepository.save(user);
		String token = jwtTokenProvider.createToken(user.getEmail(), user.getRole());
		return new AuthResponse(token, "Bearer", toDto(user));
	}

	public AuthResponse login(LoginRequest req) {
		User user = userRepository.findByEmail(req.getEmail())
				.orElseThrow(() -> new BadCredentialsException("Invalid credentials"));
		if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
			throw new BadCredentialsException("Invalid credentials");
		}
		String token = jwtTokenProvider.createToken(user.getEmail(), user.getRole());
		return new AuthResponse(token, "Bearer", toDto(user));
	}

	private static UserDto toDto(User u) {
		return new UserDto(u.getId(), u.getName(), u.getEmail(), u.getRole());
	}
}
