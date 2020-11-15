package com.scalea.enums;

public enum InitialPrivileges {
	READ_PRIVILEGE("READ_PRIVILEGE"),
	WRITE_PRIVILEGE("WRITE_PRIVILEGE");
	
	private String name;
	
	InitialPrivileges(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
