package com.noteseyfinal1.exceptions;

public class NoteNotDeletedException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	private String message;

	public NoteNotDeletedException() {
		super();
	}

	public NoteNotDeletedException(String message) {
		this.message = message;
	}
}
