package com.scribblemate.responses;

import com.scribblemate.dto.UserResponseDto;

public class LoginResponse {

	private String accessToken;
	private UserResponseDto userDto;
	private long expiresIn;
	private long accessTokenExpiresIn;

	public String getAccessToken() {
		return accessToken;
	}

	public LoginResponse setAccessToken(String accessToken) {
		this.accessToken = accessToken;
		return this;
	}

	public long getExpiresIn() {
		return expiresIn;
	}

	public LoginResponse setExpiresIn(long expiresIn) {
		this.expiresIn = expiresIn;
		return this;

	}

	public long getAccessTokenExpiresIn() {
		return accessTokenExpiresIn;
	}

	public void setAccessTokenExpiresIn(long accessTokenExpiresIn) {
		this.accessTokenExpiresIn = accessTokenExpiresIn;
	}

	public UserResponseDto getUserDto() {
		return userDto;
	}

	public LoginResponse setUserDto(UserResponseDto userDto) {
		this.userDto = userDto;
		return this;
	}

}
