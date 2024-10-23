package com.scribblemate.responses;

import com.scribblemate.dto.UserResponseDto;

public class LoginResponse {

	private UserResponseDto userDto;

	public UserResponseDto getUserDto() {
		return userDto;
	}

	public LoginResponse setUserDto(UserResponseDto userDto) {
		this.userDto = userDto;
		return this;
	}

}
