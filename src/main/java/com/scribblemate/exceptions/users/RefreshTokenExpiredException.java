package com.scribblemate.exceptions.users;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RefreshTokenExpiredException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	private String message;

	public RefreshTokenExpiredException() {
		super();
	}

	public RefreshTokenExpiredException(String message) {
		this.message = message;
	}
}
