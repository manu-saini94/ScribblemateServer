package com.scribblemate.exceptions.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import com.scribblemate.exceptions.labels.LabelAlreadyExistException;
import com.scribblemate.exceptions.labels.LabelNotDeletedException;
import com.scribblemate.exceptions.labels.LabelNotFoundException;
import com.scribblemate.exceptions.labels.LabelNotPersistedException;
import com.scribblemate.exceptions.labels.LabelNotUpdatedException;
import com.scribblemate.exceptions.labels.LabelsNotFoundException;
import com.scribblemate.exceptions.notes.CollaboratorAlreadyExistException;
import com.scribblemate.exceptions.notes.CollaboratorDoesNotExistException;
import com.scribblemate.exceptions.notes.CollaboratorNotAddedException;
import com.scribblemate.exceptions.notes.CollaboratorNotDeletedException;
import com.scribblemate.exceptions.notes.NoteNotFoundException;
import com.scribblemate.exceptions.notes.NoteNotPersistedException;
import com.scribblemate.exceptions.notes.NoteNotUpdatedException;
import com.scribblemate.exceptions.notes.NotesNotFoundException;
import com.scribblemate.exceptions.users.RefreshTokenDeletionException;
import com.scribblemate.exceptions.users.RegistrationException;
import com.scribblemate.exceptions.users.UserAlreadyExistException;
import com.scribblemate.exceptions.users.UserNotDeletedException;
import com.scribblemate.exceptions.users.UserNotFoundException;
import com.scribblemate.responses.ErrorResponse;
import com.scribblemate.utility.ResponseErrorUtils;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionController {

	private ResponseEntity<ErrorResponse> buildErrorResponse(HttpStatus status, ResponseErrorUtils error,
			String message) {
		ErrorResponse errorResponse = new ErrorResponse(status.value(), error.name(), message);
		return ResponseEntity.status(status).body(errorResponse);
	}

	@ExceptionHandler(value = BadCredentialsException.class)
	public ResponseEntity<ErrorResponse> handleBadCredentialsException(BadCredentialsException exp) {
		return buildErrorResponse(HttpStatus.UNAUTHORIZED, ResponseErrorUtils.USERNAME_OR_PASSWORD_INCORRECT,
				exp.getMessage());
	}

	@ExceptionHandler(value = AccountStatusException.class)
	public ResponseEntity<ErrorResponse> handleAccountStatusException(AccountStatusException exp) {
		return buildErrorResponse(HttpStatus.FORBIDDEN, ResponseErrorUtils.ACCOUNT_IS_LOCKED, exp.getMessage());
	}

	@ExceptionHandler(value = AccessDeniedException.class)
	public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException exp) {
		return buildErrorResponse(HttpStatus.UNAUTHORIZED, ResponseErrorUtils.NOT_AUTHORIZED_TO_ACCESS,
				exp.getMessage());
	}

	@ExceptionHandler(value = SignatureException.class)
	public ResponseEntity<ErrorResponse> handleSignatureException(SignatureException exp) {
		return buildErrorResponse(HttpStatus.FORBIDDEN, ResponseErrorUtils.JWT_SIGNATURE_INVALID, exp.getMessage());
	}

	@ExceptionHandler(value = ExpiredJwtException.class)
	public ResponseEntity<ErrorResponse> handleExpiredJwtException(ExpiredJwtException exp) {
		return buildErrorResponse(HttpStatus.FORBIDDEN, ResponseErrorUtils.JWT_TOKEN_EXPIRED, exp.getMessage());
	}
	
	@ExceptionHandler(value = RefreshTokenDeletionException.class)
	public ResponseEntity<ErrorResponse> handleTokenDeletionException(RefreshTokenDeletionException exp) {
		return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, ResponseErrorUtils.ERROR_DELETING_TOKEN, exp.getMessage());
	}

	@ExceptionHandler(value = RegistrationException.class)
	public ResponseEntity<ErrorResponse> handleRegistrationException(RegistrationException exp) {
		return buildErrorResponse(HttpStatus.UNAUTHORIZED, ResponseErrorUtils.ERROR_PERSISTING_USER, exp.getMessage());
	}

	@ExceptionHandler(value = UserAlreadyExistException.class)
	public ResponseEntity<ErrorResponse> handleUserAlreadyExistException(UserAlreadyExistException exp) {
		return buildErrorResponse(HttpStatus.CONFLICT, ResponseErrorUtils.USER_ALREADY_EXIST, exp.getMessage());
	}

	@ExceptionHandler(value = UserNotFoundException.class)
	public ResponseEntity<ErrorResponse> handleUserNotFoundException(UserNotFoundException exp) {
		return buildErrorResponse(HttpStatus.NOT_FOUND, ResponseErrorUtils.USER_NOT_FOUND, exp.getMessage());
	}

	@ExceptionHandler(value = UserNotDeletedException.class)
	public ResponseEntity<ErrorResponse> handleUserNotDeletedException(UserNotDeletedException exp) {
		return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, ResponseErrorUtils.USER_NOT_DELETED,
				exp.getMessage());
	}

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
		return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, ResponseErrorUtils.NOTE_PERSIST_ERROR,
				exp.getMessage());
	}

	@ExceptionHandler(value = NoteNotUpdatedException.class)
	public ResponseEntity<ErrorResponse> noteNotUpdatedException(NoteNotUpdatedException exp) {
		return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, ResponseErrorUtils.NOTE_UPDATE_ERROR,
				exp.getMessage());
	}

	@ExceptionHandler(value = CollaboratorNotDeletedException.class)
	public ResponseEntity<ErrorResponse> collaboratorNotDeletedException(CollaboratorNotDeletedException exp) {
		return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, ResponseErrorUtils.COLLABORATOR_DELETE_ERROR,
				exp.getMessage());
	}

	@ExceptionHandler(value = CollaboratorDoesNotExistException.class)
	public ResponseEntity<ErrorResponse> collaboratorDoesNotExistException(CollaboratorDoesNotExistException exp) {
		return buildErrorResponse(HttpStatus.NOT_FOUND, ResponseErrorUtils.COLLABORATOR_DOES_NOT_EXIST_ERROR,
				exp.getMessage());
	}

	@ExceptionHandler(value = CollaboratorNotAddedException.class)
	public ResponseEntity<ErrorResponse> collaboratorNotAddedException(CollaboratorNotAddedException exp) {
		return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, ResponseErrorUtils.COLLABORATOR_ADD_ERROR,
				exp.getMessage());
	}

	@ExceptionHandler(value = CollaboratorAlreadyExistException.class)
	public ResponseEntity<ErrorResponse> collaboratorAlreadyExistException(CollaboratorAlreadyExistException exp) {
		return buildErrorResponse(HttpStatus.CONFLICT, ResponseErrorUtils.COLLABORATOR_ALREADY_EXIST_ERROR,
				exp.getMessage());
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
		return buildErrorResponse(HttpStatus.CONFLICT, ResponseErrorUtils.LABEL_ALREADY_EXIST_ERROR, exp.getMessage());
	}

	@ExceptionHandler(value = Exception.class)
	public ResponseEntity<ErrorResponse> handleGenericException(Exception exp) {
		log.error("An unexpected error occurred", exp);
		return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, ResponseErrorUtils.INTERNAL_SERVER_ERROR,
				exp.getMessage());
	}

	/*
	 * @ExceptionHandler(value = UserInactiveException.class) public
	 * ResponseEntity<ErrorResponse>
	 * handleUserInactiveException(UserInactiveException exp) { return
	 * buildErrorResponse(HttpStatus.FORBIDDEN, ResponseErrorUtils.USER_IS_INACTIVE,
	 * exp.getMessage()); }
	 * 
	 * @ExceptionHandler(value = InternalServerError.class) public
	 * ResponseEntity<ErrorResponse> handleInternalServerError(InternalServerError
	 * exp) { return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR,
	 * ResponseErrorUtils.INTERNAL_SERVER_ERROR, exp.getMessage()); }
	 */

}
