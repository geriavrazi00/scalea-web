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
	
	public static final String VIEW_PROCESSES_HISTORIC_PRIVELEGE = "VIEW_PROCESSES_HISTORIC_PRIVELEGE";
	
	public static final String VIEW_PROFILE_PRIVILEGE = "VIEW_PROFILE_PRIVILEGE";
	public static final String UPDATE_PROFILE_PRIVILEGE = "UPDATE_PROFILE_PRIVILEGE";
	
	public static final String VIEW_FINANCIAL_ACTIVITIES_PRIVILEGE = "VIEW_FINANCIAL_ACTIVITIES_PRIVILEGE";
	
	// Configuration options
	public static final String IMAGE_PATH = "image_path";
	public static final String PRODUCTS_IMAGE_SYSTEM_PATH = "products\\";
	public static final String TAP_LOW = "tap_low";
	public static final String TAP_LOW_PERC = "tap_low_perc";
	public static final String TAP_MED = "tap_med";
	public static final String TAP_MED_PERC = "tap_med_perc";
	public static final String TAP_MAX = "tap_max";
	public static final String TAP_MAX_PERC = "tap_max_perc";
	public static final String MINIMAL_PAY = "minimal_pay";
	public static final String MAXIMUM_PAY = "maximum_pay";
	public static final String HEALTH_INSURANCE_EMPLOYER_PERC = "health_insurance_employer_perc";
	public static final String HEALTH_INSURANCE_EMPLOYEE_PERC = "health_insurance_employee_perc";
	public static final String SOCIAL_INSURANCE_EMPLOYER_PERC = "social_insurance_employer_perc";
	public static final String SOCIAL_INSURANCE_EMPLOYEE_PERC = "social_insurance_employee_perc";
	
	// Default files
	public static final String PRODUCTS_DEFAULT_IMAGE = "default-product.jpg";
	
	// When a record is disabled, we need to add something to a field of it that must be unique. So this way, a new record with that same field can be created
	public static final String DISABLED_STRING = "disabled------";
}
