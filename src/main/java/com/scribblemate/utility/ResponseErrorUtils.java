package com.scribblemate.utility;

public enum ResponseErrorUtils {
	DESCRIPTION("description"), NOTE_PERSIST_ERROR("Error occurred while persisting note"),
	NOTE_NOT_FOUND("Note not found with the id"), NOTES_NOT_FOUND("Notes not found"),
	NOTE_UPDATE_ERROR("Error occurred while updating note"), NOTES_NOT_FOUND_FOR_USER("Notes not found for the user"),
	ERROR_FETCHING_NOTES_FOR_USER("Error occurred while fetching notes for user"),
	USERNAME_OR_PASSWORD_INCORRECT("The username or password is incorrect"), ACCOUNT_IS_LOCKED("The account is locked"),
	NOT_AUTHORIZED_TO_ACCESS("You are not authorized to access this resource"),
	ERROR_DELETING_TOKEN("Error Deleting refresh token"), REFRESH_TOKEN_EXPIRED("The Refresh token has expired"),
	REFRESH_TOKEN_MISSING_OR_INVALID("Refresh token is missing or invalid"),
	INTERNAL_SERVER_ERROR("Unknown internal server error"),
	ERROR_PERSISTING_USER("Error occurred while persisting user"), USER_NOT_FOUND("User not found"),
	USER_ALREADY_EXIST("User already exists with this email"),
	LABEL_PERSIST_ERROR("Error occurred while persisting label"), LABELS_NOT_FOUND("Labels not found"),
	LABEL_DELETE_ERROR("Error while deleting Labels"), LABEL_UPDATE_ERROR("Error updating Labels"),
	LABEL_ALREADY_EXIST_ERROR("Label already Exist"), USER_NOT_DELETED("User not deleted"),
	USER_IS_INACTIVE("User is Inactive"), COLLABORATOR_DOES_NOT_EXIST_ERROR("User with this email does not exist"),
	COLLABORATOR_ALREADY_EXIST_ERROR("User with this email already exist for note"),
	COLLABORATOR_DELETE_ERROR("Error occurred while deleting collaborator "),
	COLLABORATOR_ADD_ERROR("Error occurred while adding collaborator"), TOKEN_EXPIRED("The JWT token has expired"),
	JWT_SIGNATURE_INVALID("The JWT signature is invalid"), TOKEN_MISSING_OR_INVALID("Token is missing or invalid"),
	TOKEN_DELETION_FAILED("Token deletion failed!");

	private final String message;

	ResponseErrorUtils(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}
}
