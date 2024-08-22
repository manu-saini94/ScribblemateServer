package com.scribblemate.exceptions.labels;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LabelNotDeletedException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	private String message;

	public LabelNotDeletedException() {
		super();
	}

	public LabelNotDeletedException(String message) {
		this.message = message;
	}
}
