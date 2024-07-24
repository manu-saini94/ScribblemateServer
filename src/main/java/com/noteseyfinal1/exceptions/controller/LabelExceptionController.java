package com.noteseyfinal1.exceptions.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.noteseyfinal1.exceptions.labels.LabelNotDeletedException;
import com.noteseyfinal1.exceptions.labels.LabelNotFoundException;
import com.noteseyfinal1.exceptions.labels.LabelNotPersistedException;
import com.noteseyfinal1.exceptions.labels.LabelNotUpdatedException;
import com.noteseyfinal1.exceptions.labels.LabelsNotFoundException;
import com.noteseyfinal1.responses.ErrorResponse;
import com.noteseyfinal1.utility.ResponseErrorUtils;

@RestControllerAdvice
public class LabelExceptionController {

	@ExceptionHandler(value = LabelNotFoundException.class)
	public ResponseEntity<ErrorResponse> labelNotFoundException(LabelNotFoundException exp) {
		ErrorResponse errorResponse = new ErrorResponse(HttpStatus.NOT_FOUND.value(),
				ResponseErrorUtils.LABEL_NOT_FOUND);		
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
	}

	@ExceptionHandler(value = LabelsNotFoundException.class)
	public ResponseEntity<ErrorResponse> labelsNotFoundException(LabelsNotFoundException exp) {
		ErrorResponse errorResponse = new ErrorResponse(HttpStatus.NOT_FOUND.value(),
				ResponseErrorUtils.LABEL_NOT_FOUND);
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
	}

	@ExceptionHandler(value = LabelNotPersistedException.class)
	public ResponseEntity<ErrorResponse> labelNotPersistedException(LabelNotPersistedException exp) {
		ErrorResponse errorResponse = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(),
				ResponseErrorUtils.LABEL_PERSIST_ERROR);
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
	}

	@ExceptionHandler(value = LabelNotDeletedException.class)
	public ResponseEntity<ErrorResponse> labelNotDeletedException(LabelNotDeletedException exp) {
		ErrorResponse errorResponse = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(),
				ResponseErrorUtils.LABEL_PERSIST_ERROR);
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
	}

	@ExceptionHandler(value = LabelNotUpdatedException.class)
	public ResponseEntity<ErrorResponse> labelNotUpdatedException(LabelNotUpdatedException exp) {
		ErrorResponse errorResponse = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(),
				ResponseErrorUtils.LABEL_PERSIST_ERROR);
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
	}
}
