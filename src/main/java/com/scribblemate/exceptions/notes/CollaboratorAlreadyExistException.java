package com.scribblemate.exceptions.notes;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CollaboratorAlreadyExistException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	private String message;

	public CollaboratorAlreadyExistException() {
		super();
	}

	public CollaboratorAlreadyExistException(String message) {
		this.message = message;
	}

}
