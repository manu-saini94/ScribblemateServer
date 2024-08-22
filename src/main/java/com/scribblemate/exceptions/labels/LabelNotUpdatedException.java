package com.scribblemate.exceptions.labels;

public class LabelNotUpdatedException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	private String message;

	public LabelNotUpdatedException() {
		super();
	}

	public LabelNotUpdatedException(String message) {
		this.message = message;
	}
}
