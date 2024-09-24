package com.scribblemate.exceptions.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.scribblemate.exceptions.labels.LabelAlreadyExistException;
import com.scribblemate.exceptions.labels.LabelNotDeletedException;
import com.scribblemate.exceptions.labels.LabelNotFoundException;
import com.scribblemate.exceptions.labels.LabelNotPersistedException;
import com.scribblemate.exceptions.labels.LabelNotUpdatedException;
import com.scribblemate.exceptions.labels.LabelsNotFoundException;
import com.scribblemate.responses.ErrorResponse;
import com.scribblemate.utility.ResponseErrorUtils;

@RestControllerAdvice
public class LabelExceptionController {

	private ResponseEntity<ErrorResponse> buildErrorResponse(HttpStatus status, ResponseErrorUtils error,
			String message) {
		ErrorResponse errorResponse = new ErrorResponse(status.value(), error.name(), message);
		return ResponseEntity.status(status).body(errorResponse);
	}

	@ExceptionHandler(value = LabelNotFoundException.class)
	public ResponseEntity<ErrorResponse> handleLabelNotFoundException(LabelNotFoundException exp) {
		return buildErrorResponse(HttpStatus.NOT_FOUND, ResponseErrorUtils.LABELS_NOT_FOUND, exp.getMessage());
	}

	@ExceptionHandler(value = LabelsNotFoundException.class)
	public ResponseEntity<ErrorResponse> handleLabelsNotFoundException(LabelsNotFoundException exp) {
		return buildErrorResponse(HttpStatus.NOT_FOUND, ResponseErrorUtils.LABELS_NOT_FOUND, exp.getMessage());
	}

	@ExceptionHandler(value = LabelNotPersistedException.class)
	public ResponseEntity<ErrorResponse> handleLabelNotPersistedException(LabelNotPersistedException exp) {
		return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, ResponseErrorUtils.LABEL_PERSIST_ERROR,
				exp.getMessage());
	}

	@ExceptionHandler(value = LabelNotDeletedException.class)
	public ResponseEntity<ErrorResponse> handleLabelNotDeletedException(LabelNotDeletedException exp) {
		return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, ResponseErrorUtils.LABEL_DELETE_ERROR,
				exp.getMessage());
	}

	@ExceptionHandler(value = LabelNotUpdatedException.class)
	public ResponseEntity<ErrorResponse> handleLabelNotUpdatedException(LabelNotUpdatedException exp) {
		return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, ResponseErrorUtils.LABEL_UPDATE_ERROR,
				exp.getMessage());
	}
	
	@ExceptionHandler(value = LabelAlreadyExistException.class)
	public ResponseEntity<ErrorResponse> handleLabelAlreadyExistException(LabelAlreadyExistException exp) {
		return buildErrorResponse(HttpStatus.CONFLICT, ResponseErrorUtils.LABEL_ALREADY_EXIST_ERROR,
				exp.getMessage());
	}
}
