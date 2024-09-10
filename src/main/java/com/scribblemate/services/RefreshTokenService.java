package com.scribblemate.services;

import java.time.Instant;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.scribblemate.entities.RefreshToken;
import com.scribblemate.entities.User;
import com.scribblemate.repositories.RefreshTokenRepository;
import com.scribblemate.repositories.UserRepository;

@Service
public class RefreshTokenService {

	@Value("${security.jwt.refresh-expiration-time}")
	private Long refreshTokenDurationMs;

	@Autowired
	private RefreshTokenRepository refreshTokenRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private JwtAuthenticationService jwtAuthenticationService;

	public Optional<RefreshToken> findByToken(String token) {
		return refreshTokenRepository.findByToken(token);
	}

	public RefreshToken createRefreshToken(User authUser) {
		RefreshToken refreshToken = new RefreshToken();
		User user = userRepository.findByEmail(authUser.getEmail()).get();
		refreshToken.setUser(user);
		refreshToken.setToken(jwtAuthenticationService.generateRefreshToken(user));
		refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));
		return refreshTokenRepository.save(refreshToken);
	}

	public boolean isRefreshTokenExpired(RefreshToken token) {
		return token.getExpiryDate().isBefore(Instant.now());
	}

	public void deleteByToken(String token) {
		refreshTokenRepository.deleteByToken(token);
	}

	public void deleteById(Integer userId) {
		refreshTokenRepository.deleteById(userId);
	}
}
