package com.scribblemate.exceptions.notes;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CollaboratorDoesNotExistException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	private String message;

	public CollaboratorDoesNotExistException() {
		super();
	}

	public CollaboratorDoesNotExistException(String message) {
		this.message = message;
	}
}
