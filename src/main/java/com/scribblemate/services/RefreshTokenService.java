package com.scribblemate.services;

import java.time.Instant;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import com.scribblemate.entities.RefreshToken;
import com.scribblemate.entities.User;
import com.scribblemate.exceptions.notes.NoteNotFoundException;
import com.scribblemate.exceptions.users.RefreshTokenDeletionException;
import com.scribblemate.repositories.RefreshTokenRepository;
import com.scribblemate.repositories.UserRepository;
import com.scribblemate.utility.NoteUtils;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
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

	public int deleteTokenForUser(User user) {
		try {
			return refreshTokenRepository.deleteByUser(user);
		} catch (DataAccessException dae) {
			log.error("Failed to delete token due to database error.");
			throw new RefreshTokenDeletionException("Failed to delete token due to database error.");
		} catch (IllegalArgumentException iae) {
			log.error("Invalid user provided for token deletion.");
			throw new RefreshTokenDeletionException("Invalid user provided for token deletion.");
		} catch (Exception e) {
			log.error("An unexpected error occurred while deleting tokens.");
			throw new RefreshTokenDeletionException("An unexpected error occurred while deleting tokens.");
		}
	}
}
