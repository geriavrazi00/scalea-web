package com.scalea.enums;

import com.scalea.utils.Constants;

public enum ApplicationRoles {
	ROLE_ADMIN(Constants.ROLE_ADMIN),
	ROLE_USER(Constants.ROLE_USER);
	
	private String name;
	
	ApplicationRoles(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
