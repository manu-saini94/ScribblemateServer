package com.noteseyfinal1.exceptions.users;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserNotDeletedException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	private String message;

	public UserNotDeletedException() {
		super();
	}

	public UserNotDeletedException(String message) {
		this.message = message;
	}
}
