package com.scalea.security;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import com.scalea.utils.Constants;

public class AuthenticationRedirectHandler implements AuthenticationSuccessHandler {
	
	private Map<String, String> privilegeTargetUrlMap = new LinkedHashMap<>();
	private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
    
	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, 
			Authentication authentication) throws IOException, ServletException {
		this.mapPrivilegesToTargetUrls();
		String targetUrl = this.findTargerUrl(authentication);
		
		redirectStrategy.sendRedirect(request, response, targetUrl);
	}
	
	private void mapPrivilegesToTargetUrls() {
		// Areas
		privilegeTargetUrlMap.put(Constants.VIEW_AREAS_PRIVILEGE, "/areas");
		privilegeTargetUrlMap.put(Constants.UPSERT_AREAS_PRIVILEGE, "/areas");
		privilegeTargetUrlMap.put(Constants.DELETE_AREAS_PRIVILEGE, "/areas");
		
		// Employees
		privilegeTargetUrlMap.put(Constants.VIEW_EMPLOYEES_PRIVILEGE, "/employees");
		privilegeTargetUrlMap.put(Constants.UPSERT_EMPLOYEES_PRIVILEGE, "/employees");
		privilegeTargetUrlMap.put(Constants.DELETE_EMPLOYEES_PRIVILEGE, "/employees");
		privilegeTargetUrlMap.put(Constants.UPLOAD_EMPLOYEES_PRIVILEGE, "/employees");
		
		// Statistics
		privilegeTargetUrlMap.put(Constants.VIEW_GENERAL_STATISTICS_PRIVILEGE, "/statistics");
		
		// Products
		privilegeTargetUrlMap.put(Constants.VIEW_PRODUCTS_PRIVILEGE, "/products");
		privilegeTargetUrlMap.put(Constants.UPSERT_PRODUCTS_PRIVILEGE, "/products");
		privilegeTargetUrlMap.put(Constants.DELETE_PRODUCTS_PRIVILEGE, "/products");
		
		// Financial activities
		privilegeTargetUrlMap.put(Constants.VIEW_FINANCIAL_ACTIVITIES_PRIVILEGE, "/finances");
		
		// Processes
		privilegeTargetUrlMap.put(Constants.VIEW_PROCESSES_PRIVILEGE, "/processes");
		privilegeTargetUrlMap.put(Constants.MANAGE_PROCESSES_PRIVILEGE, "/processes");
		
		// Control
		privilegeTargetUrlMap.put(Constants.MANAGE_CONTROL_PRIVILEGE, "/control");
		
		// Profile
		privilegeTargetUrlMap.put(Constants.VIEW_PROFILE_PRIVILEGE, "/profile");
		privilegeTargetUrlMap.put(Constants.UPDATE_PROFILE_PRIVILEGE, "/profile");
		
		// Users
		privilegeTargetUrlMap.put(Constants.VIEW_USERS_PRIVILEGE, "/users");
		privilegeTargetUrlMap.put(Constants.UPSERT_USERS_PRIVILEGE, "/users");
		privilegeTargetUrlMap.put(Constants.PASSWORDS_USERS_PRIVILEGE, "/users");
		privilegeTargetUrlMap.put(Constants.DELETE_USERS_PRIVILEGE, "/users");
		
		// Roles
		privilegeTargetUrlMap.put(Constants.VIEW_ROLES_PRIVILEGE, "/roles");
		privilegeTargetUrlMap.put(Constants.UPSERT_ROLES_PRIVILEGE, "/roles");
		privilegeTargetUrlMap.put(Constants.DELETE_ROLES_PRIVILEGE, "/roles");
	}
	
	private String findTargerUrl(Authentication authentication) {
		final Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
		
		for (Map.Entry<String, String> entry : privilegeTargetUrlMap.entrySet()) {
    		String privilege = entry.getKey();
    		
    		for (final GrantedAuthority grantedAuthority : authorities) {
    	    	String authorityName = grantedAuthority.getAuthority();
    	    	
    	        if(privilege.equals(authorityName)) {
    	            return privilegeTargetUrlMap.get(authorityName);
    	        }
    	    }
    	}
		
	    throw new IllegalStateException();
	}
}
