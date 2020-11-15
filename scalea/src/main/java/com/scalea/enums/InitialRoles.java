package com.scalea.enums;

public enum InitialRoles {
	ROLE_ADMIN("ROLE_ADMIN"),
	ROLE_USER("ROLE_USER");
	
	private String name;
	
	InitialRoles(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
