package com.scribblemate.exceptions.notes;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NoteNotPersistedException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	private String message;

	public NoteNotPersistedException() {
		super();
	}

	public NoteNotPersistedException(String message) {
		this.message = message;
	}

}
