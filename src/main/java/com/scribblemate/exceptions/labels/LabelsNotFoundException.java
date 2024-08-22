package com.scribblemate.exceptions.labels;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LabelsNotFoundException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	private String message;

	public LabelsNotFoundException() {
		super();
	}

	public LabelsNotFoundException(String message) {
		this.message = message;
	}
}
