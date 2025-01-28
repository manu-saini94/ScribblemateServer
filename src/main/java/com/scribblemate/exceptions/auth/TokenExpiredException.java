package com.scribblemate.exceptions.auth;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TokenExpiredException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	private String message;

	public TokenExpiredException() {
		super();
	}

	public TokenExpiredException(String message) {
		this.message = message;
	}
}
