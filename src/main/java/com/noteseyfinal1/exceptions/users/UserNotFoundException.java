package com.noteseyfinal1.exceptions.users;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserNotFoundException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	private String message;

	public UserNotFoundException() {
		super();
	}

	public UserNotFoundException(String message) {
		this.message = message;
	}
}
