package com.scribblemate.exceptions.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.scribblemate.exceptions.users.RefreshTokenExpiredException;
import com.scribblemate.exceptions.users.RefreshTokenMissingOrInvalidException;
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

	private ResponseEntity<ErrorResponse> buildErrorResponse(HttpStatus status, ResponseErrorUtils error, String message) {
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

	@ExceptionHandler(value = RefreshTokenExpiredException.class)
	public ResponseEntity<ErrorResponse> handleRefreshTokenExpiredException(RefreshTokenExpiredException exp) {
		return buildErrorResponse(HttpStatus.UNAUTHORIZED, ResponseErrorUtils.REFRESH_TOKEN_EXPIRED, exp.getMessage());
	}

	@ExceptionHandler(value = RefreshTokenMissingOrInvalidException.class)
	public ResponseEntity<ErrorResponse> handleRefreshTokenMissingOrInvalidException(
			RefreshTokenMissingOrInvalidException exp) {
		return buildErrorResponse(HttpStatus.UNAUTHORIZED, ResponseErrorUtils.REFRESH_TOKEN_MISSING_OR_INVALID,
				exp.getMessage());
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
