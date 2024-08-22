package com.scribblemate.exceptions.labels;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LabelNotPersistedException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	private String message;

	public LabelNotPersistedException() {
		super();
	}

	public LabelNotPersistedException(String message) {
		this.message = message;
	}
}
