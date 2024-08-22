package com.scribblemate.exceptions.users;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserInactiveException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	private String message;

	public UserInactiveException() {
		super();
	}

	public UserInactiveException(String message) {
		this.message = message;
	}
}
