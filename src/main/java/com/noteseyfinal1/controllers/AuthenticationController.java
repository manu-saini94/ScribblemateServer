package com.noteseyfinal1.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.noteseyfinal1.dto.LoginDto;
import com.noteseyfinal1.dto.RegistrationDto;
import com.noteseyfinal1.entities.User;
import com.noteseyfinal1.exceptions.users.UserInactiveException;
import com.noteseyfinal1.responses.LoginResponse;
import com.noteseyfinal1.services.AuthenticationService;
import com.noteseyfinal1.services.JwtAuthenticationService;
import com.noteseyfinal1.utility.Utils.Status;

@RequestMapping("/auth")
@RestController

public class AuthenticationController {
	private final JwtAuthenticationService jwtService;

	private final AuthenticationService authenticationService;

	public AuthenticationController(JwtAuthenticationService jwtService, AuthenticationService authenticationService) {
		this.jwtService = jwtService;
		this.authenticationService = authenticationService;
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
	public ResponseEntity<LoginResponse> authenticate(@RequestBody LoginDto loginUserDto) {
		User authenticatedUser = authenticationService.authenticate(loginUserDto);
		if (authenticatedUser.getStatus().equals(Status.INACTIVE))
			throw new UserInactiveException();
		String jwtToken = jwtService.generateToken(authenticatedUser);
		LoginResponse loginResponse = new LoginResponse().setToken(jwtToken)
				.setExpiresIn(jwtService.getExpirationTime());

		return ResponseEntity.ok(loginResponse);
	}

}
