package com.scribblemate.exceptions.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.scribblemate.exceptions.notes.NoteNotFoundException;
import com.scribblemate.exceptions.notes.NoteNotPersistedException;
import com.scribblemate.exceptions.notes.NoteNotUpdatedException;
import com.scribblemate.exceptions.notes.NotesNotFoundException;
import com.scribblemate.responses.ErrorResponse;
import com.scribblemate.utility.ResponseErrorUtils;

@RestControllerAdvice
public class NoteExceptionController {

	   @ExceptionHandler(value = NoteNotFoundException.class)
	    public ResponseEntity<ErrorResponse> noteNotFoundException(NoteNotFoundException exp) {
	        return buildErrorResponse(HttpStatus.NOT_FOUND, ResponseErrorUtils.NOTE_NOT_FOUND, exp.getMessage());
	    }

	    @ExceptionHandler(value = NotesNotFoundException.class)
	    public ResponseEntity<ErrorResponse> notesNotFoundException(NotesNotFoundException exp) {
	        return buildErrorResponse(HttpStatus.NOT_FOUND, ResponseErrorUtils.NOTES_NOT_FOUND, exp.getMessage());
	    }

	    @ExceptionHandler(value = NoteNotPersistedException.class)
	    public ResponseEntity<ErrorResponse> noteNotPersistedException(NoteNotPersistedException exp) {
	        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, ResponseErrorUtils.NOTE_PERSIST_ERROR, exp.getMessage());
	    }

	    @ExceptionHandler(value = NoteNotUpdatedException.class)
	    public ResponseEntity<ErrorResponse> noteNotUpdatedException(NoteNotUpdatedException exp) {
	        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, ResponseErrorUtils.NOTE_UPDATE_ERROR, exp.getMessage());
	    }

	    private ResponseEntity<ErrorResponse> buildErrorResponse(HttpStatus status, ResponseErrorUtils error, String message) {
			ErrorResponse errorResponse = new ErrorResponse(status.value(), error.name(), message);
			return ResponseEntity.status(status).body(errorResponse);
		}

}
