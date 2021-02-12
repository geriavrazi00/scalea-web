package com.scalea.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value=HttpStatus.NOT_FOUND, reason="Role Not Found") //404
public class RoleNotFoundException extends GenericException {

	private static final long serialVersionUID = -3189593124849846947L;

	public RoleNotFoundException(String message) {
		super(message);
	}
}
