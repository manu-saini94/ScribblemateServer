package com.scribblemate.services;

import java.time.LocalDateTime;
import java.util.List;
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
import org.springframework.web.bind.annotation.RequestBody;
import com.scribblemate.dto.LoginDto;
import com.scribblemate.dto.RegistrationDto;
import com.scribblemate.entities.RefreshToken;
import com.scribblemate.entities.User;
import com.scribblemate.exceptions.users.RegistrationException;
import com.scribblemate.exceptions.users.UserAlreadyExistException;
import com.scribblemate.exceptions.users.UserInactiveException;
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
	private JwtAuthenticationService jwtService;

	@Autowired
	private RefreshTokenService refreshTokenService;

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
		User user = new User();
		List<Object[]> fieldsList = userRepository.findSpecificFieldsByEmail(input.getEmail());
		if (fieldsList != null) {
			for (Object[] row : fieldsList) {
				Long id = (Long) row[0];
				LocalDateTime createdAt = (LocalDateTime) row[1];
				String email = (String) row[2];
				String fullName = (String) row[3];
				String profilePicture = (String) row[4];
				Status status = (Status) row[5];
				LocalDateTime updatedAt = (LocalDateTime) row[6];
				user.setId(id);
				user.setCreatedAt(createdAt);
				user.setEmail(email);
				user.setFullName(fullName);
				user.setProfilePicture(profilePicture);
				user.setStatus(status);
				user.setUpdatedAt(updatedAt);
			}
			return user;
		} else {
			log.error(UserUtils.ERROR_USER_NOT_FOUND);
			throw new UserNotFoundException();
		}
	}

	public User authenticate(@RequestBody LoginDto loginUserDto, HttpServletResponse response) {
		User authenticatedUser = authenticate(loginUserDto);
		if (authenticatedUser.getStatus().equals(Status.INACTIVE))
			throw new UserInactiveException();
		String jwtAccessToken = jwtService.generateToken(authenticatedUser);
		Cookie newAccessTokenCookie = createAndReturnCookieWithAccessToken(jwtAccessToken);
		RefreshToken refreshToken = refreshTokenService.createRefreshToken(authenticatedUser);
		Cookie newRefreshTokenCookie = createAndReturnCookieWithRefreshToken(refreshToken);
		addCookies(response, newAccessTokenCookie, newRefreshTokenCookie);
		return authenticatedUser;
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
			StringBuilder cookieHeader = new StringBuilder();
			cookieHeader.append(cookie.getName()).append("=").append(cookie.getValue()).append("; Max-Age=")
					.append(cookie.getMaxAge()).append("; Path=").append(cookie.getPath());
//			if ("refreshToken".equals(cookie.getName())) {
//				cookieHeader.append("; HttpOnly");
//			}
			cookieHeader.append("; SameSite=none; secure");
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
