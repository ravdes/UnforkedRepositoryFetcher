package com.ravdes.unforkedrepositories.exceptions;

public class UserNotFoundException extends RuntimeException{
	public UserNotFoundException(String message) {
		super(message);
	}
}
