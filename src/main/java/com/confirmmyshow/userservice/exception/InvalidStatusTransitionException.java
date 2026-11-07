package com.confirmmyshow.userservice.exception;

public class InvalidStatusTransitionException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public InvalidStatusTransitionException(String msg) {
		super(msg);
	}

	
}
