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
	public static final String PASSWORDS_USERS_PRIVILEGE = "PASSWORDS_USERS_PRIVILEGE";
	public static final String DELETE_USERS_PRIVILEGE	= "DELETE_USERS_PRIVILEGE";
	
	public static final String VIEW_AREAS_PRIVILEGE	= "VIEW_AREAS_PRIVILEGE";
	public static final String UPSERT_AREAS_PRIVILEGE = "UPSERT_AREAS_PRIVILEGE";
	public static final String DELETE_AREAS_PRIVILEGE	= "DELETE_AREAS_PRIVILEGE";
	
	public static final String VIEW_EMPLOYEES_PRIVILEGE = "VIEW_EMPLOYEES_PRIVILEGE";
	public static final String UPSERT_EMPLOYEES_PRIVILEGE = "UPSERT_EMPLOYEES_PRIVILEGE";
	public static final String DELETE_EMPLOYEES_PRIVILEGE = "DELETE_EMPLOYEES_PRIVILEGE";
	
	public static final String VIEW_VACANCIES_PRIVILEGE = "VIEW_VACANCIES_PRIVILEGE";
	public static final String UPSERT_VACANCIES_PRIVILEGE = "UPSERT_VACANCIES_PRIVILEGE";
	public static final String DELETE_VACANCIES_PRIVILEGE = "DELETE_VACANCIES_PRIVILEGE";
	
	public static final String VIEW_PROCESSES_PRIVILEGE = "VIEW_PROCESSES_PRIVILEGE";
	public static final String MANAGE_PROCESSES_PRIVILEGE = "MANAGE_PROCESSES_PRIVILEGE";
	
	public static final String VIEW_PRODUCTS_PRIVILEGE = "VIEW_PRODUCTS_PRIVILEGE";
	public static final String UPSERT_PRODUCTS_PRIVILEGE = "UPSERT_PRODUCTS_PRIVILEGE";
	public static final String DELETE_PRODUCTS_PRIVILEGE = "DELETE_PRODUCTS_PRIVILEGE";
	
	// Configuration options
	public static final String IMAGE_PATH = "image_path";
	public static final String PRODUCTS_IMAGE_SYSTEM_PATH = "products\\";
	
	// Default files
	public static final String PRODUCTS_DEFAULT_IMAGE = "default-product.jpg";
}
