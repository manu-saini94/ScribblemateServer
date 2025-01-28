package com.scribblemate.dto;

import java.time.LocalDateTime;
import com.scribblemate.utility.Utils;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserResponseDto {

	private Long id;
	
	private String fullName;

	private String email;

	private Utils.Status status;

	private LocalDateTime createdAt;

	private LocalDateTime updatedAt;

	private String profilePicture;

}
