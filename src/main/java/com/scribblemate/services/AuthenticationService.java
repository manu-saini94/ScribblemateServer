package com.scribblemate.services;

import java.util.Optional;
import java.util.Random;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.scribblemate.dto.LoginDto;
import com.scribblemate.dto.RegistrationDto;
import com.scribblemate.entities.RefreshToken;
import com.scribblemate.entities.User;
import com.scribblemate.exceptions.users.RegistrationException;
import com.scribblemate.exceptions.users.UserAlreadyExistException;
import com.scribblemate.exceptions.users.UserNotFoundException;
import com.scribblemate.repositories.UserRepository;
import com.scribblemate.utility.UserUtils;
import com.scribblemate.utility.Utils.Status;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AuthenticationService {

	private Random random = new Random(1000);

	@Value("${security.jwt.refresh-expiration-time}")
	private Long refreshTokenDurationMs;

	@Value("${security.jwt.access-expiration-time}")
	private Long accessTokenDurationMs;

	@Autowired
	private EmailService emailService;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private AuthenticationManager authenticationManager;

	public User signUp(RegistrationDto input) {
		Optional<User> existingUser = userRepository.findByEmail(input.getEmail());
		if (existingUser.isPresent())
			throw new UserAlreadyExistException();
		User newUser = null;
		try {
			newUser = new User().setFullName(input.getFullName()).setEmail(input.getEmail())
					.setPassword(passwordEncoder.encode(input.getPassword())).setStatus(Status.ACTIVE);
			return userRepository.save(newUser);
		} catch (Exception exp) {
			log.error(UserUtils.ERROR_PERSISTING_USER, newUser);
			throw new RegistrationException();
		}
	}

	public User authenticate(LoginDto input) {
		Authentication authentication = authenticationManager
				.authenticate(new UsernamePasswordAuthenticationToken(input.getEmail(), input.getPassword()));
		SecurityContextHolder.getContext().setAuthentication(authentication);
		return userRepository.findByEmail(input.getEmail()).orElseThrow(() -> {
			log.error(UserUtils.ERROR_USER_NOT_FOUND);
			return new UserNotFoundException();
		});
	}

	public Cookie createAndReturnCookieWithRefreshToken(RefreshToken token) {
		Cookie newRefreshTokenCookie = new Cookie("refreshToken", token.getToken());
		newRefreshTokenCookie.setHttpOnly(true);
		newRefreshTokenCookie.setPath("/");
		newRefreshTokenCookie.setSecure(true);
		newRefreshTokenCookie.setMaxAge((int) (refreshTokenDurationMs / 1000));
		return newRefreshTokenCookie;
	}

	public Cookie createAndReturnCookieWithAccessToken(String token) {
		Cookie newAccessTokenCookie = new Cookie("accessToken", token);
		newAccessTokenCookie.setHttpOnly(true);
		newAccessTokenCookie.setSecure(true);
		newAccessTokenCookie.setPath("/");
		newAccessTokenCookie.setMaxAge((int) (accessTokenDurationMs / 1000));
		return newAccessTokenCookie;
	}

	public void addCookies(HttpServletResponse response, Cookie... cookies) {
		for (Cookie cookie : cookies) {
			// Start building the cookie header
			StringBuilder cookieHeader = new StringBuilder();
			cookieHeader.append(cookie.getName()).append("=").append(cookie.getValue()).append("; Max-Age=")
					.append(cookie.getMaxAge()).append("; Path=").append(cookie.getPath());

			// Add HttpOnly if the cookie is for a refresh token
			if ("refreshToken".equals(cookie.getName())) {
				cookieHeader.append("; HttpOnly");
			}

			// Add SameSite attribute
			cookieHeader.append("; SameSite=none; secure");

			// Add the cookie to the response
			response.addHeader("Set-Cookie", cookieHeader.toString());
		}
	}

	public boolean forgot(String email) {
		int otp = random.nextInt(10000);
		String subject = "OTP from notesy";
		String message = "<h1> OTP = " + otp + "</h1>";
		String to = email;
		boolean flag = emailService.sendEmail(subject, message, to);
		return flag;
	}
}
