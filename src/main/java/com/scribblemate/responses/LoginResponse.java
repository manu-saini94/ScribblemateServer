package com.scribblemate.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
import com.scribblemate.dto.UserResponseDto;


public class LoginResponse {

	private String accessToken;
	private UserResponseDto userDto;
	private long expiresIn;
	private long accessTokenExpiresIn;

	public UserResponseDto getUserDto() {
		return userDto;
	}

	public LoginResponse setUserDto(UserResponseDto userDto) {
		this.userDto = userDto;
		return this;
	}

}
