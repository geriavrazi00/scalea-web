package com.scalea.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value=HttpStatus.NOT_FOUND, reason="User Not Found") //404
public class UserNotFoundException extends GenericException {

	private static final long serialVersionUID = 8385020074165943786L;

	public UserNotFoundException(String messageKey) {
		super(messageKey);
	}

}
