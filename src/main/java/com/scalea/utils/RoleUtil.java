package com.scalea.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.scalea.configurations.Messages;
import com.scalea.entities.Privilege;

public class RoleUtil {	
	public static Map<String, List<Privilege>> groupPrivileges(Iterable<Privilege> privileges, Messages messages) {
		Map<String, List<Privilege>> groupedPrivileges = new TreeMap<>();
		groupedPrivileges.put(messages.get("messages.role.home.privilege"), new ArrayList<>());
		groupedPrivileges.put(messages.get("messages.role.barcode.privilege"), new ArrayList<>());
		groupedPrivileges.put(messages.get("messages.role.role.privilege"), new ArrayList<>());
		groupedPrivileges.put(messages.get("messages.role.user.privilege"), new ArrayList<>());
		groupedPrivileges.put(messages.get("messages.role.area.privilege"), new ArrayList<>());
		groupedPrivileges.put(messages.get("messages.role.employee.privilege"), new ArrayList<>());
		groupedPrivileges.put(messages.get("messages.role.vacancy.privilege"), new ArrayList<>());
		groupedPrivileges.put(messages.get("messages.role.process.privilege"), new ArrayList<>());
		groupedPrivileges.put(messages.get("messages.role.product.privilege"), new ArrayList<>());
		groupedPrivileges.put(messages.get("messages.role.historic.privilege"), new ArrayList<>());
		groupedPrivileges.put(messages.get("messages.role.profile.privilege"), new ArrayList<>());
		groupedPrivileges.put(messages.get("messages.role.financial.privilege"), new ArrayList<>());
		groupedPrivileges.put(messages.get("messages.role.statistic.privilege"), new ArrayList<>());
		groupedPrivileges.put(messages.get("messages.role.control.privilege"), new ArrayList<>());
		
		for (Privilege privilege : privileges) {
			if (Arrays.asList(Constants.HOME_PRIVILEGES).contains(privilege.getName())) {
				groupedPrivileges.get(messages.get("messages.role.home.privilege")).add(privilege);
			} else if (Arrays.asList(Constants.BARCODE_PRIVILEGES).contains(privilege.getName())) {
				groupedPrivileges.get(messages.get("messages.role.barcode.privilege")).add(privilege);
			} else if (Arrays.asList(Constants.ROLE_PRIVILEGES).contains(privilege.getName())) {
				groupedPrivileges.get(messages.get("messages.role.role.privilege")).add(privilege);
			} else if (Arrays.asList(Constants.USER_PRIVILEGES).contains(privilege.getName())) {
				groupedPrivileges.get(messages.get("messages.role.user.privilege")).add(privilege);
			} else if (Arrays.asList(Constants.AREA_PRIVILEGES).contains(privilege.getName())) {
				groupedPrivileges.get(messages.get("messages.role.area.privilege")).add(privilege);
			} else if (Arrays.asList(Constants.EMPLOYEE_PRIVILEGES).contains(privilege.getName())) {
				groupedPrivileges.get(messages.get("messages.role.employee.privilege")).add(privilege);
			} else if (Arrays.asList(Constants.VACANCY_PRIVILEGES).contains(privilege.getName())) {
				groupedPrivileges.get(messages.get("messages.role.vacancy.privilege")).add(privilege);
			} else if (Arrays.asList(Constants.PROCESS_PRIVILEGES).contains(privilege.getName())) {
				groupedPrivileges.get(messages.get("messages.role.process.privilege")).add(privilege);
			} else if (Arrays.asList(Constants.PRODUCT_PRIVILEGES).contains(privilege.getName())) {
				groupedPrivileges.get(messages.get("messages.role.product.privilege")).add(privilege);
			} else if (Arrays.asList(Constants.HISTORIC_PRIVILEGES).contains(privilege.getName())) {
				groupedPrivileges.get(messages.get("messages.role.historic.privilege")).add(privilege);
			} else if (Arrays.asList(Constants.PROFILE_PRIVILEGES).contains(privilege.getName())) {
				groupedPrivileges.get(messages.get("messages.role.profile.privilege")).add(privilege);
			} else if (Arrays.asList(Constants.FINANCIAL_PRIVILEGES).contains(privilege.getName())) {
				groupedPrivileges.get(messages.get("messages.role.financial.privilege")).add(privilege);
			} else if (Arrays.asList(Constants.STATISTIC_PRIVILEGES).contains(privilege.getName())) {
				groupedPrivileges.get(messages.get("messages.role.statistic.privilege")).add(privilege);
			} else if (Arrays.asList(Constants.CONTROL_PRIVILEGES).contains(privilege.getName())) {
				groupedPrivileges.get(messages.get("messages.role.control.privilege")).add(privilege);
			}
		}
		
		return groupedPrivileges;
	}
}
