package com.scalea.utils;

public interface Constants {
	// Default administrator data
	public static final String DEFAULT_ADMIN_USERNAME = "admin";
	public static final String DEFAULT_ADMIN_PASSWORD = "123456789";
	public static final String DEFAULT_ADMIN_FIRSTNAME = "Admin";
	public static final String DEFAULT_ADMIN_LASTNAME = "Admin";
	public static final String DEFAULT_ADMIN_PHONE = "+355673688616";
	
	// Roles
	public static final String ROLE_USER 	= "ROLE_USER";
	public static final String ROLE_ADMIN 	= "ROLE_ADMIN";
	
	// Privileges
	public static final String VIEW_HOME_PRIVILEGE 		= "VIEW_HOME_PRIVILEGE";
	public static final String VIEW_BARCODE_PRIVILEGE 	= "VIEW_BARCODE_PRIVILEGE";
	public static final String VIEW_ROLES_PRIVILEGE 	= "VIEW_ROLES_PRIVILEGE";
	public static final String UPSERT_ROLES_PRIVILEGE 	= "UPSERT_ROLES_PRIVILEGE";
	public static final String DELETE_ROLES_PRIVILEGE 	= "DELETE_ROLES_PRIVILEGE";
	public static final String VIEW_USERS_PRIVILEGE		= "VIEW_USERS_PRIVILEGE";
	public static final String UPSERT_USERS_PRIVILEGE	= "UPSERT_USERS_PRIVILEGE";
	public static final String DELETE_USERS_PRIVILEGE	= "DELETE_USERS_PRIVILEGE";
}
