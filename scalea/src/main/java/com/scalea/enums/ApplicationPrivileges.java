package com.scalea.enums;

import com.scalea.utils.Constants;

public enum ApplicationPrivileges {
	// Home
	VIEW_HOME_PRIVILEGE(Constants.VIEW_HOME_PRIVILEGE, true),
	
	// Barcodes
	VIEW_BARCODE_PRIVILEGE(Constants.VIEW_BARCODE_PRIVILEGE, true),
	
	// Roles
	VIEW_ROLES_PRIVILEGE(Constants.VIEW_ROLES_PRIVILEGE, false),
	UPSERT_ROLES_PRIVILEGE(Constants.UPSERT_ROLES_PRIVILEGE, false),
	DELETE_ROLES_PRIVILEGE(Constants.DELETE_ROLES_PRIVILEGE, false),
	
	// Users
	VIEW_USERS_PRIVILEGE(Constants.VIEW_USERS_PRIVILEGE, false),
	UPSERT_USERS_PRIVILEGE(Constants.UPSERT_USERS_PRIVILEGE, false),
	PASSWORDS_USERS_PRIVILEGE(Constants.PASSWORDS_USERS_PRIVILEGE, false),
	DELETE_USERS_PRIVILEGE(Constants.DELETE_USERS_PRIVILEGE, false);
	
	private String name;
	private boolean forUsers;
	
	ApplicationPrivileges(String name, boolean forUsers) {
		this.name = name;
		this.forUsers = forUsers;
	}

	public boolean isForUsers() {
		return forUsers;
	}

	public String getName() {
		return name;
	}
}
