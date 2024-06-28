package com.noteseyfinal1.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.noteseyfinal1.entities.User;
import com.noteseyfinal1.responses.SuccessResponse;
import com.noteseyfinal1.services.UserService;
import com.noteseyfinal1.utility.ResponseSuccessUtils;

import jakarta.servlet.http.HttpServletRequest;

@RequestMapping("/users")
@RestController
public class UserController {

	@Autowired
	private UserService userService;

	@GetMapping("/me")
	public ResponseEntity<User> authenticatedUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		User currentUser = (User) authentication.getPrincipal();

		return ResponseEntity.ok(currentUser);
	}

	@GetMapping("/")
	public ResponseEntity<List<User>> allUsers() {
		List<User> users = userService.allUsers();

		return ResponseEntity.ok(users);
	}

	@DeleteMapping("/delete")
	public ResponseEntity<SuccessResponse> deleteUser(HttpServletRequest httpRequest) {
		User user = userService.getEmailFromJwt(httpRequest);
		boolean isDeleted = userService.deleteUser(user);
		return ResponseEntity.ok()
				.body(new SuccessResponse(HttpStatus.OK.value(), ResponseSuccessUtils.USER_DELETE_SUCCESS, isDeleted));
	}
}
