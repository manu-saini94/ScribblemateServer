package com.scribblemate.responses;

import com.scribblemate.dto.UserResponseDto;

public class LoginResponse {
	private String token;

	private UserResponseDto userDto;

	private long expiresIn;

	public LoginResponse() {
		super();
	}

	public LoginResponse(String token, long expiresIn) {
		super();
		this.token = token;
		this.expiresIn = expiresIn;
	}

	public String getToken() {
		return token;
	}

	public LoginResponse setToken(String token) {
		this.token = token;
		return this;
	}

	public long getExpiresIn() {
		return expiresIn;
	}

	public LoginResponse setExpiresIn(long expiresIn) {
		this.expiresIn = expiresIn;
		return this;

	}

	public UserResponseDto getUserDto() {
		return userDto;
	}

	public LoginResponse setUserDto(UserResponseDto userDto) {
		this.userDto = userDto;
		return this;
	}

}
