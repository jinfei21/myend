package com.pingan.jinke.infra.padis.exceptions;

public class MaxRedirectionsException extends DataException {
	private static final long serialVersionUID = 3878126572474819403L;

	public MaxRedirectionsException(Throwable cause) {
		super(cause);
	}

	public MaxRedirectionsException(String message, Throwable cause) {
		super(message, cause);
	}

	public MaxRedirectionsException(String message) {
		super(message);
	}
}
