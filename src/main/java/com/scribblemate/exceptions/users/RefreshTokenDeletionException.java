package com.scribblemate.exceptions.users;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RefreshTokenDeletionException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	private String message;

	public RefreshTokenDeletionException() {
		super();
	}

	public RefreshTokenDeletionException(String message) {
		this.message = message;
	}
}
