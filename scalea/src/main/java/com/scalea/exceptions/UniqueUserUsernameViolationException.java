package com.scalea.exceptions;

public class UniqueUserUsernameViolationException extends GenericException {

	private static final long serialVersionUID = 4457121159931874222L;

	public UniqueUserUsernameViolationException(String messageKey) {
		super(messageKey);
	}

}
