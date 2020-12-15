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
	DELETE_USERS_PRIVILEGE(Constants.DELETE_USERS_PRIVILEGE, false), 
	
	// Areas
	VIEW_AREAS_PRIVILEGE(Constants.VIEW_AREAS_PRIVILEGE, true),
	UPSERT_AREAS_PRIVILEGE(Constants.UPSERT_AREAS_PRIVILEGE, true),
	DELETE_AREAS_PRIVILEGE(Constants.DELETE_AREAS_PRIVILEGE, true),
	
	// Employees
	VIEW_EMPLOYEES_PRIVILEGE(Constants.VIEW_EMPLOYEES_PRIVILEGE, true),
	UPSERT_EMPLOYEES_PRIVILEGE(Constants.UPSERT_EMPLOYEES_PRIVILEGE, true),
	DELETE_EMPLOYEES_PRIVILEGE(Constants.DELETE_EMPLOYEES_PRIVILEGE, true),
	
	// Vacancies
	VIEW_VACANCIES_PRIVILEGE(Constants.VIEW_VACANCIES_PRIVILEGE, true),
	UPSERT_VACANCIES_PRIVILEGE(Constants.UPSERT_VACANCIES_PRIVILEGE, true),
	DELETE_VACANCIES_PRIVILEGE(Constants.DELETE_VACANCIES_PRIVILEGE, true);
	
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
