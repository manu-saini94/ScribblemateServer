package com.scribblemate.exceptions.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.scribblemate.exceptions.users.RegistrationException;
import com.scribblemate.exceptions.users.UserAlreadyExistException;
import com.scribblemate.exceptions.users.UserNotDeletedException;
import com.scribblemate.exceptions.users.UserNotFoundException;
import com.scribblemate.responses.ErrorResponse;
import com.scribblemate.utility.ResponseErrorUtils;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;

@RestControllerAdvice
public class GlobalExceptionController {

	@ExceptionHandler(value = BadCredentialsException.class)
	public ResponseEntity<ErrorResponse> badCredentialsException(BadCredentialsException exp) {
		ErrorResponse errorResponse = new ErrorResponse(HttpStatus.UNAUTHORIZED.value(),
				 ResponseErrorUtils.USERNAME_OR_PASSWORD_INCORRECT);		
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);		
	}

	@ExceptionHandler(value = AccountStatusException.class)
	public ResponseEntity<ErrorResponse> accountStatusException(AccountStatusException exp) {
		ErrorResponse errorResponse = new ErrorResponse(HttpStatus.FORBIDDEN.value(),
				ResponseErrorUtils.ACCOUNT_IS_LOCKED);		
		return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);		
	}

	@ExceptionHandler(value = AccessDeniedException.class)
	public ResponseEntity<ErrorResponse> accessDeniedException(AccessDeniedException exp) {	
		ErrorResponse errorResponse = new ErrorResponse(HttpStatus.UNAUTHORIZED.value(),
				 ResponseErrorUtils.NOT_AUTHORIZED_TO_ACCESS);		
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);	
	}

	@ExceptionHandler(value = SignatureException.class)
	public ResponseEntity<ErrorResponse> signatureException(SignatureException exp) {		
		ErrorResponse errorResponse = new ErrorResponse(HttpStatus.FORBIDDEN.value(),
				ResponseErrorUtils.JWT_SIGNATURE_INVALID);		
		return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);	
	}

	@ExceptionHandler(value = ExpiredJwtException.class)
	public ResponseEntity<ErrorResponse> expiredJwtException(ExpiredJwtException exp) {
		ErrorResponse errorResponse = new ErrorResponse(HttpStatus.FORBIDDEN.value(),
				 ResponseErrorUtils.JWT_TOKEN_EXPIRED);		
		return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);	
	}

	@ExceptionHandler(value = RegistrationException.class)
	public ResponseEntity<ErrorResponse> registrationException(RegistrationException exp) {
		ErrorResponse errorResponse = new ErrorResponse(HttpStatus.UNAUTHORIZED.value(),
				ResponseErrorUtils.ERROR_PERSISTING_USER);		
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);			
	}
	
	@ExceptionHandler(value = UserAlreadyExistException.class)
	public ResponseEntity<ErrorResponse> userAlreadyExistException(UserAlreadyExistException exp) {	
		ErrorResponse errorResponse = new ErrorResponse(HttpStatus.CONFLICT.value(),
				ResponseErrorUtils.USER_ALREADY_EXIST);		
		return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);	
	}

	@ExceptionHandler(value = UserNotFoundException.class)
	public ResponseEntity<ErrorResponse> userNotFoundException(UserNotFoundException exp) {
		ErrorResponse errorResponse = new ErrorResponse(HttpStatus.NOT_FOUND.value(),
				ResponseErrorUtils.USER_NOT_FOUND);		
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
	}

	@ExceptionHandler(value = UserNotDeletedException.class)
	public ResponseEntity<ErrorResponse> userNotDeletedException(UserNotDeletedException exp) {
		ErrorResponse errorResponse = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(),ResponseErrorUtils.USER_NOT_DELETED);	
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);	
	}
//
//	@ExceptionHandler(value = UserInactiveException.class)
//	public ResponseEntity<ErrorResponse> userInactiveException(UserInactiveException exp) {
//		ErrorResponse errorResponse = new ErrorResponse(HttpStatus.FORBIDDEN.value(),
//				ResponseErrorUtils.USER_IS_INACTIVE);		
//		return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);	
//	}

//	@ExceptionHandler(value = InternalServerError.class)
//	public ResponseEntity<ErrorResponse> internalServerError(InternalServerError exp) {
//		ErrorResponse errorResponse = new ErrorResponse(HttpStatus.FORBIDDEN.value(),
//				ResponseErrorUtils.USER_IS_INACTIVE);		
//		return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);	
//		ProblemDetail errorDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(500), exp.getMessage());
//		errorDetail.setProperty(ResponseErrorUtils.DESCRIPTION, ResponseErrorUtils.INTERNAL_SERVER_ERROR);
//		return errorDetail;
//	}

}
