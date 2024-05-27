package com.noteseyfinal1.exceptions;

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
