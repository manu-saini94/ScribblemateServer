package com.scribblemate.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.scribblemate.entities.User;
import com.scribblemate.responses.SuccessResponse;
import com.scribblemate.services.UserService;
import com.scribblemate.utility.ResponseSuccessUtils;

import jakarta.servlet.http.HttpServletRequest;

@RequestMapping("/api/v1/users")
@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
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
		User user = userService.getUserFromHttpRequest(httpRequest);
		boolean isDeleted = userService.deleteUser(user);
		return ResponseEntity.ok()
				.body(new SuccessResponse(HttpStatus.OK.value(), ResponseSuccessUtils.USER_DELETE_SUCCESS, isDeleted));
	}
}
