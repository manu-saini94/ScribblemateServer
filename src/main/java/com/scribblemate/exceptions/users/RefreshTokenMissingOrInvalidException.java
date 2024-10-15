package com.scribblemate.exceptions.users;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RefreshTokenMissingOrInvalidException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	private String message;

	public RefreshTokenMissingOrInvalidException() {
		super();
	}

	public RefreshTokenMissingOrInvalidException(String message) {
		this.message = message;
	}
}
