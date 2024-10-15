package com.scribblemate.controllers;

import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.scribblemate.dto.LoginDto;
import com.scribblemate.dto.RegistrationDto;
import com.scribblemate.entities.RefreshToken;
import com.scribblemate.dto.UserResponseDto;
import com.scribblemate.entities.User;
import com.scribblemate.exceptions.users.RefreshTokenExpiredException;
import com.scribblemate.exceptions.users.RefreshTokenMissingOrInvalidException;
import com.scribblemate.exceptions.users.UserInactiveException;
import com.scribblemate.responses.LoginResponse;
import com.scribblemate.responses.SuccessResponse;
import com.scribblemate.services.AuthenticationService;
import com.scribblemate.services.JwtAuthenticationService;
import com.scribblemate.services.RefreshTokenService;
import com.scribblemate.services.UserService;
import com.scribblemate.utility.ResponseSuccessUtils;
import com.scribblemate.utility.Utils.Status;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RequestMapping("/api/v1/auth")
@RestController
@CrossOrigin(origins = "http://localhost:3000", allowedHeaders = "*", allowCredentials="true")
public class AuthenticationController {

	private final UserService userService;

	private final JwtAuthenticationService jwtService;

	private final AuthenticationService authenticationService;

	private final RefreshTokenService refreshTokenService;

	public AuthenticationController(JwtAuthenticationService jwtService, AuthenticationService authenticationService,
			RefreshTokenService refreshTokenService, UserService userService) {
		this.jwtService = jwtService;
		this.authenticationService = authenticationService;
		this.refreshTokenService = refreshTokenService;

	public AuthenticationController(JwtAuthenticationService jwtService, AuthenticationService authenticationService,
			UserService userService) {
		this.jwtService = jwtService;
		this.authenticationService = authenticationService;
		this.userService = userService;
	}

	@PostMapping("/signup")
	public ResponseEntity<User> register(@RequestBody RegistrationDto registerDto) {
		User registeredUser = authenticationService.signUp(registerDto);
		return ResponseEntity.ok(registeredUser);
	}

	@PostMapping("/forgot")
	public ResponseEntity<Boolean> forgotPassword(@RequestParam String email) {
		boolean isSent = authenticationService.forgot(email);
		return ResponseEntity.ok(isSent);
	}

	@PostMapping("/login")
	public ResponseEntity<SuccessResponse> authenticate(@RequestBody LoginDto loginUserDto) {
		User authenticatedUser = authenticationService.authenticate(loginUserDto);
		if (authenticatedUser.getStatus().equals(Status.INACTIVE))
			throw new UserInactiveException();
		String jwtToken = jwtService.generateToken(authenticatedUser);
		UserResponseDto userResponseDto = userService.getUserDtoFromUser(authenticatedUser);
		LoginResponse loginResponse = new LoginResponse().setToken(jwtToken)
				.setExpiresIn(jwtService.getExpirationTime()).setUserDto(userResponseDto);
		return ResponseEntity.ok().body(
				new SuccessResponse(HttpStatus.OK.value(), ResponseSuccessUtils.USER_LOGIN_SUCCESS, loginResponse));


	public ResponseEntity<User> authenticate(@RequestBody LoginDto loginUserDto, HttpServletResponse response) {
		User authenticatedUser = authenticationService.authenticate(loginUserDto);
		if (authenticatedUser.getStatus().equals(Status.INACTIVE))
			throw new UserInactiveException();
		// Generate access token
		String jwtAccessToken = jwtService.generateToken(authenticatedUser);
		Cookie newAccessTokenCookie = authenticationService.createAndReturnCookieWithAccessToken(jwtAccessToken);
		RefreshToken refreshToken = refreshTokenService.createRefreshToken(authenticatedUser);
		Cookie newRefreshTokenCookie = authenticationService.createAndReturnCookieWithRefreshToken(refreshToken);
		authenticationService.addCookies(response, newAccessTokenCookie, newRefreshTokenCookie);
		return ResponseEntity.ok(authenticatedUser);
	}

	@PostMapping("/refresh-token")
	public ResponseEntity<SuccessResponse> refreshToken(HttpServletRequest request, HttpServletResponse response) {
		Cookie[] cookies = request.getCookies();
		String refreshTokenValue = null;
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if ("refreshToken".equals(cookie.getName())) {
					refreshTokenValue = cookie.getValue();
					break;
				}
			}
		}
		if (refreshTokenValue == null) {
			throw new RefreshTokenMissingOrInvalidException("Refresh token is missing or invalid");
		}

		Optional<RefreshToken> tokenOptional = refreshTokenService.findByToken(refreshTokenValue);

		if (tokenOptional.isEmpty() || refreshTokenService.isRefreshTokenExpired(tokenOptional.get())) {
			throw new RefreshTokenExpiredException("Refresh token has expired");
		}

		RefreshToken token = tokenOptional.get();
		User user = token.getUser();

		String jwtAccessToken = jwtService.generateToken(user);
		Cookie newAccessTokenCookie = authenticationService.createAndReturnCookieWithAccessToken(jwtAccessToken);
		response.addCookie(newAccessTokenCookie);

		// Optionally, create a new refresh token
		RefreshToken newRefreshToken = refreshTokenService.createRefreshToken(user);
		Cookie newRefreshTokenCookie = authenticationService.createAndReturnCookieWithRefreshToken(newRefreshToken);
		response.addCookie(newRefreshTokenCookie);

		return ResponseEntity.ok()
				.body(new SuccessResponse(HttpStatus.OK.value(), ResponseSuccessUtils.TOKEN_REFRESH_SUCCESS, user));
	}

	@PostMapping("/logout")
	public ResponseEntity<SuccessResponse> logoutUser(HttpServletRequest request, HttpServletResponse response) {
		Cookie[] cookiesArray = request.getCookies();
		User user = null;
		if (cookiesArray != null) {
			for (Cookie cookie : cookiesArray) {
				if ("accessToken".equals(cookie.getName()) || "refreshToken".equals(cookie.getName())) {
					if ("accessToken".equals(cookie.getName())) {
						String accessTokenString = cookie.getValue();
						user = userService.getUserFromJwt(accessTokenString);
					}
					Cookie invalidCookie = new Cookie(cookie.getName(), null);
					invalidCookie.setHttpOnly("refreshToken".equals(cookie.getName())); // HttpOnly for refresh token
					invalidCookie.setPath("/"); // Ensure path matches the original cookie
					invalidCookie.setMaxAge(0); // Set cookie to expire immediately
					response.addCookie(invalidCookie);
				}
			}
		}
		if (user != null) {
			refreshTokenService.deleteById(user.getId());
		}
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth != null) {
			new SecurityContextLogoutHandler().logout(request, response, auth);
		}
		SecurityContextHolder.clearContext();
		return ResponseEntity.ok()
				.body(new SuccessResponse(HttpStatus.OK.value(), ResponseSuccessUtils.USER_LOGOUT_SUCCESS, user));
	}

}
