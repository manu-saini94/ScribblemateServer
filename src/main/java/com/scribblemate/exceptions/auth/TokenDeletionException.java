package com.scribblemate.exceptions.auth;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TokenDeletionException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	private String message;

	public TokenDeletionException() {
		super();
	}

	public TokenDeletionException(String message) {
		this.message = message;
	}
}
