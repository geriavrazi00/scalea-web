package com.scalea.models.dto;

import com.scalea.utils.Utils;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class FinanceDTO implements Comparable<FinanceDTO> {
	private double grossSalary;
	private double healthInsuranceSalary;
	private double socialInsuranceSalary;
	private double employerSocialInsurance;
	private double employerHealthInsurance;
	private double employeeSocialInsurance;
	private double employeeHealthInsurance;
	private double progressiveTax;
	private double totalEmploymentCost;
	private double netSalary;
	private EmployeeFinancialDTO employee;
	private boolean belowMinimalWage;
	
	public String getFormattedGrossSalary() {
		return Utils.thousandSeparatorDouble(grossSalary);
	}
	
	public String getFormattedHealthInsuranceSalary() {
		return Utils.thousandSeparatorDouble(healthInsuranceSalary);
	}
	
	public String getFormattedSocialInsuranceSalary() {
		return Utils.thousandSeparatorDouble(socialInsuranceSalary);
	}
	
	public String getFormattedEmployerSocialInsurance() {
		return Utils.thousandSeparatorDouble(employerSocialInsurance);
	}
	
	public String getFormattedEmployerHealthInsurance() {
		return Utils.thousandSeparatorDouble(employerHealthInsurance);
	}
	
	public String getFormattedEmployeeSocialInsurance() {
		return Utils.thousandSeparatorDouble(employeeSocialInsurance);
	}
	
	public String getFormattedEmployeeHealthInsurance() {
		return Utils.thousandSeparatorDouble(employeeHealthInsurance);
	}
	
	public String getFormattedProgressiveTax() {
		return Utils.thousandSeparatorDouble(progressiveTax);
	}
	
	public String getFormattedTotalEmploymentCost() {
		return Utils.thousandSeparatorDouble(totalEmploymentCost);
	}
	
	public String getFormattedNetSalary() {
		return Utils.thousandSeparatorDouble(netSalary);
	}
	
	public String getFormattedEmployerInsuranceTotal() {
		return Utils.thousandSeparatorDouble(employerSocialInsurance + employerHealthInsurance);
	}
	
	public String getFormattedEmployeeInsuranceTotal() {
		return Utils.thousandSeparatorDouble(employeeSocialInsurance + employeeHealthInsurance);
	}
	
	public String getFormattedInsuranceTotal() {
		return Utils.thousandSeparatorDouble(employerSocialInsurance + employerHealthInsurance + employeeSocialInsurance + employeeHealthInsurance);
	}
	
	@Override
	public int compareTo(FinanceDTO financeDto) {
		if (financeDto == null || financeDto.getEmployee() == null || this.getEmployee() == null) {
			return 0;
		}
		
		String thisFullName = this.getEmployee().getFirstName() + " " + this.getEmployee().getLastName();
		String otherFullName = financeDto.getEmployee().getFirstName() + " " + financeDto.getEmployee().getLastName();
		return thisFullName.compareTo(otherFullName);
	}
}
