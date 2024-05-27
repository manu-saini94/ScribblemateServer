package com.noteseyfinal1.responses;

public class LoginResponse {
	private String token;

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

}
