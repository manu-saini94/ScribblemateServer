package com.scribblemate.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.scribblemate.dto.LoginDto;
import com.scribblemate.dto.RegistrationDto;
import com.scribblemate.dto.UserResponseDto;
import com.scribblemate.entities.User;
import com.scribblemate.responses.SuccessResponse;
import com.scribblemate.services.AuthenticationService;
import com.scribblemate.services.UserService;
import com.scribblemate.utility.ResponseSuccessUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RequestMapping("${app.api.prefix}/auth")
@RestController
@CrossOrigin(origins = "${allowed.origin}", allowedHeaders = "*", allowCredentials = "true")
public class AuthenticationController {

	private final UserService userService;

	private final AuthenticationService authenticationService;

	public AuthenticationController(AuthenticationService authenticationService, UserService userService) {
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
	public ResponseEntity<SuccessResponse> authenticate(@RequestBody LoginDto loginUserDto,
			HttpServletResponse response) {
		User authenticatedUser = authenticationService.authenticate(loginUserDto, response);
		UserResponseDto userResponseDto = userService.getUserDtoFromUser(authenticatedUser);
		return ResponseEntity.ok().body(
				new SuccessResponse(HttpStatus.OK.value(), ResponseSuccessUtils.USER_LOGIN_SUCCESS, userResponseDto));
	}

	@PostMapping("/refresh-token")
	public ResponseEntity<SuccessResponse> refreshToken(HttpServletRequest request, HttpServletResponse response) {
		User authUser = authenticationService.refreshAuthToken(request, response);
		UserResponseDto userResponseDto = userService.getUserDtoFromUser(authUser);
		return ResponseEntity.ok().body(new SuccessResponse(HttpStatus.OK.value(),
				ResponseSuccessUtils.TOKEN_REFRESH_SUCCESS, userResponseDto));
	}

	@GetMapping("/validate")
	public ResponseEntity<SuccessResponse> validateUser(HttpServletRequest httpRequest,
			HttpServletResponse httpResponse) {
		User authUser = userService.getUserFromHttpRequest(httpRequest);
		UserResponseDto userResponseDto = userService.getUserDtoFromUser(authUser);
		return ResponseEntity.ok().body(new SuccessResponse(HttpStatus.OK.value(),
				ResponseSuccessUtils.USER_VALIDATION_SUCCESS, userResponseDto));
	}

	@DeleteMapping("/logout")
	public ResponseEntity<SuccessResponse> logoutUser(HttpServletRequest request, HttpServletResponse response) {
		authenticationService.logoutAuthUser(request, response);
		return ResponseEntity.ok()
				.body(new SuccessResponse(HttpStatus.OK.value(), ResponseSuccessUtils.USER_LOGOUT_SUCCESS, true));
	}

}
