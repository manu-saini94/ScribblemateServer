package com.scribblemate.exceptions.notes;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NoteNotUpdatedException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	private String message;

	public NoteNotUpdatedException() {
		super();
	}

	public NoteNotUpdatedException(String message) {
		this.message = message;
	}
}
