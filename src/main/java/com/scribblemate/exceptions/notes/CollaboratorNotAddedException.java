package com.scribblemate.exceptions.notes;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CollaboratorNotAddedException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	private String message;

	public CollaboratorNotAddedException() {
		super();
	}

	public CollaboratorNotAddedException(String message) {
		this.message = message;
	}

}
