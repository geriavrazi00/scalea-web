package com.scalea.exceptions;

public class UniqueRoleNameViolationException extends GenericException {

	private static final long serialVersionUID = 455886869198426414L;

	public UniqueRoleNameViolationException(String message) {
		super(message);
	}
}
