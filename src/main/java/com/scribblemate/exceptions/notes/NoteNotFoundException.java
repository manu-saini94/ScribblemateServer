package com.scribblemate.exceptions.notes;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NoteNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	private String message;

	public NoteNotFoundException() {
		super();
	}

	public NoteNotFoundException(String message) {
		this.message = message;
	}

}
