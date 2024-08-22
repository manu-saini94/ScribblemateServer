package com.scribblemate.responses;


public class ErrorResponse {
	private int messagecode;
	private String message;
	private Object object;

	public int getMessagecode() {
		return messagecode;
	}

	public void setMessagecode(int messagecode) {
		this.messagecode = messagecode;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Object getObject() {
		return object;
	}

	public void setObject(Object object) {
		this.object = object;
	}

	public ErrorResponse(int messagecode, String message, Object object) {
		super();
		this.messagecode = messagecode;
		this.message = message;
		this.object = object;
	}
	
	

	public ErrorResponse(int messagecode, String message) {
		super();
		this.messagecode = messagecode;
		this.message = message;
	}

	public ErrorResponse() {
		super();
	}

}
