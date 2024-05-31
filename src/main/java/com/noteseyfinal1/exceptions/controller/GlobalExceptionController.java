package com.noteseyfinal1.exceptions.controller;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpServerErrorException.InternalServerError;

import com.noteseyfinal1.exceptions.users.RegistrationException;
import com.noteseyfinal1.exceptions.users.UserNotFoundException;
import com.noteseyfinal1.utility.ResponseErrorUtils;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;

@RestControllerAdvice
public class GlobalExceptionController {

	@ExceptionHandler(value = BadCredentialsException.class)
	public ProblemDetail badCredentialsException(BadCredentialsException exp) {
		exp.printStackTrace();
		ProblemDetail errorDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(401), exp.getMessage());
		errorDetail.setProperty(ResponseErrorUtils.DESCRIPTION, ResponseErrorUtils.USERNAME_OR_PASSWORD_INCORRECT);
		return errorDetail;
	}

	@ExceptionHandler(value = AccountStatusException.class)
	public ProblemDetail accountStatusException(AccountStatusException exp) {
		exp.printStackTrace();
		ProblemDetail errorDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(403), exp.getMessage());
		errorDetail.setProperty(ResponseErrorUtils.DESCRIPTION, ResponseErrorUtils.ACCOUNT_IS_LOCKED);
		return errorDetail;
	}

	@ExceptionHandler(value = AccessDeniedException.class)
	public ProblemDetail accessDeniedException(AccessDeniedException exp) {
		exp.printStackTrace();
		ProblemDetail errorDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(403), exp.getMessage());
		errorDetail.setProperty(ResponseErrorUtils.DESCRIPTION, ResponseErrorUtils.NOT_AUTHORIZED_TO_ACCESS);
		return errorDetail;
	}

	@ExceptionHandler(value = SignatureException.class)
	public ProblemDetail signatureException(SignatureException exp) {
		exp.printStackTrace();
		ProblemDetail errorDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(403), exp.getMessage());
		errorDetail.setProperty(ResponseErrorUtils.DESCRIPTION, ResponseErrorUtils.JWT_SIGNATURE_INVALID);
		return errorDetail;
	}

	@ExceptionHandler(value = ExpiredJwtException.class)
	public ProblemDetail expiredJwtException(ExpiredJwtException exp) {
		exp.printStackTrace();
		ProblemDetail errorDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(403), exp.getMessage());
		errorDetail.setProperty(ResponseErrorUtils.DESCRIPTION, ResponseErrorUtils.JWT_TOKEN_EXPIRED);
		return errorDetail;
	}
	
	@ExceptionHandler(value = RegistrationException.class)
	public ProblemDetail registrationException(RegistrationException exp) {
		exp.printStackTrace();
		ProblemDetail errorDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(401), exp.getMessage());
		errorDetail.setProperty(ResponseErrorUtils.DESCRIPTION, ResponseErrorUtils.ERROR_PERSISTING_USER_OR_COLLABORATOR);
		return errorDetail;
	}
	
	@ExceptionHandler(value = UserNotFoundException.class)
	public ProblemDetail userNotFoundException(UserNotFoundException exp) {
		exp.printStackTrace();
		ProblemDetail errorDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(401), exp.getMessage());
		errorDetail.setProperty(ResponseErrorUtils.DESCRIPTION, ResponseErrorUtils.USER_NOT_FOUND);
		return errorDetail;
	}

	@ExceptionHandler(value = InternalServerError.class)
	public ProblemDetail internalServerError(InternalServerError exp) {
		exp.printStackTrace();
		ProblemDetail errorDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(500), exp.getMessage());
		errorDetail.setProperty(ResponseErrorUtils.DESCRIPTION, ResponseErrorUtils.INTERNAL_SERVER_ERROR);
		return errorDetail;
	}

}
