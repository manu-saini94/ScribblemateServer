package com.scribblemate.exceptions.notes;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NotesNotFoundException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	private String message;

	public NotesNotFoundException() {
		super();
	}

	public NotesNotFoundException(String message) {
		this.message = message;
	}
}
