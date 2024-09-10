package com.scribblemate.exceptions.notes;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CollaboratorNotDeletedException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	private String message;

	public CollaboratorNotDeletedException() {
		super();
	}

	public CollaboratorNotDeletedException(String message) {
		this.message = message;
	}

}
