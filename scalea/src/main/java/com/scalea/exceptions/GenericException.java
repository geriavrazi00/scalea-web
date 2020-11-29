package com.scalea.exceptions;

public class GenericException extends Exception {

	private static final long serialVersionUID = -8033078030433182655L;
	
	public GenericException(String messageKey) {
		super(messageKey);
	}
}
