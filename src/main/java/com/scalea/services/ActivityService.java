package com.scalea.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.scalea.entities.Activity;
import com.scalea.entities.Area;
import com.scalea.entities.Employee;
import com.scalea.exceptions.GenericException;
import com.scalea.models.dto.ActivityAggregatorDTO;
import com.scalea.models.dto.DailyActivityDTO;
import com.scalea.models.dto.DailyActivityDetailDTO;
import com.scalea.models.dto.EmployeeFinancialDTO;
import com.scalea.models.dto.FinanceDTO;
import com.scalea.repositories.ActivityRepository;
import com.scalea.utils.Constants;
import com.scalea.utils.Utils;

@Service
@Transactional
public class ActivityService {
	private ActivityRepository activityRepo;
	private ConfigurationService configurationService;

	@Autowired
	public ActivityService(ActivityRepository activityRepo, ConfigurationService configurationService) {
		this.activityRepo = activityRepo;
		this.configurationService = configurationService;
	}
	
	public Optional<Activity> findById(Long id) {
		return activityRepo.findById(id);
	}
	
	public Optional<ActivityAggregatorDTO> findActivityAggByIds(Integer month, Integer year, Long employeeId) throws GenericException {
		if (month == null) throw new GenericException("The month is not available!");
		if (year == null) throw new GenericException("The year is not available!");
		if (employeeId == null) throw new GenericException("The employee is not available!");
		
		return activityRepo.findActivityAggByIds(month, year, employeeId);
	}
	
	public Page<ActivityAggregatorDTO> findAllActivities(int month, int year, Pageable pageable) throws NumberFormatException, Exception {
		return activityRepo.findSumOfWeightForProductAndVacancy(month, year, pageable);
	}
	
	public List<FinanceDTO> findAllFinancialActivities(List<ActivityAggregatorDTO> activities) throws NumberFormatException, Exception {
		double tapLow = Double.valueOf(configurationService.findValueByName(Constants.TAP_LOW));
//		double tapLowPerc = Double.valueOf(configurationService.findValueByName(Constants.TAP_LOW_PERC));
		double tapMed = Double.valueOf(configurationService.findValueByName(Constants.TAP_MED));
		double tapMedPerc = Double.valueOf(configurationService.findValueByName(Constants.TAP_MED_PERC));
		double tapMax = Double.valueOf(configurationService.findValueByName(Constants.TAP_MAX));
		double tapMaxPerc = Double.valueOf(configurationService.findValueByName(Constants.TAP_MAX_PERC));
		double minimalPay = Double.valueOf(configurationService.findValueByName(Constants.MINIMAL_PAY));
		double maximumPay = Double.valueOf(configurationService.findValueByName(Constants.MAXIMUM_PAY));
		double healthInsuranceEmployer = Double.valueOf(configurationService.findValueByName(Constants.HEALTH_INSURANCE_EMPLOYER_PERC));
		double healthInsuranceEmployee = Double.valueOf(configurationService.findValueByName(Constants.HEALTH_INSURANCE_EMPLOYEE_PERC));
		double socialInsuranceEmployer = Double.valueOf(configurationService.findValueByName(Constants.SOCIAL_INSURANCE_EMPLOYER_PERC));
		double socialInsuranceEmployee = Double.valueOf(configurationService.findValueByName(Constants.SOCIAL_INSURANCE_EMPLOYEE_PERC));
		
		List<FinanceDTO> financialData = new ArrayList<>();
		
		for (ActivityAggregatorDTO activity: activities) {
			financialData.add(calculateFinancialData(activity, tapLow, tapMed, tapMedPerc, tapMax, tapMaxPerc, minimalPay, maximumPay, 
				healthInsuranceEmployer, healthInsuranceEmployee, socialInsuranceEmployer, socialInsuranceEmployee));
		}
		
		return financialData;
	}
	
	/* 
	 * Qe te behet llogaritja e pages neto duhen pasur parasysh disa faktore:
	 * 1. Paga bruto
	 * 2. Pagesa e sigurimeve shoqerore. Pagesa e sigurimeve shoqerore behet nje pjese nga punedhenesi dhe kjo nuk ndikon ne page dhe pjesa e paguar nga punedhenesi qe zbritet nga paga bruto dhe perben 9.5% te saj. Paga minimale qe merret si baze per llogaritjen e sigurimeve shoqerore dhe shendetesore eshte 30 000 leke ndersa paga maksimale qe zbatohet vetem per sigurimet shoqerore eshte 114 670 leke. Per pagat mbi kete vlere nuk ka rritje te vleres se sigurimeve shoqerore.
	 * 3. Sigurimet shendetesore. Ashtu si sigurimet shendetesore ato paguhen nje pjese nga punedhenesi dhe pjesa tjeter nga paga e punemarresit. Konkretisht 1.7%  
	 * e pages bruto shkon per sigurimet shendetesore. Ne llogaritjen e pages neto kjo shume do te zbritet nga paga bruto.
	 * 4. Tatimi mbi te ardhurat. Ky tatim zbatohet nga te gjitha burimet e te ardhurave te individit. Ne rastin e nje punemarresi ai zbatohet mbi pagen. Bruto. 
	 * Vlera ne perqindje e ketij tatimi varet nga niveli i pages.
	 * i. Per pagat deri ne 30 000 leke tatimi mbi te ardhurat eshte 0%.
	 * ii. Per pagat mbi 30 000 deri ne 150 000 leke, ky tatim eshte 13% e vleres mbi 30 000 leke.
	 * iii. Per pagat mbi 150 000 leke tatimi eshte 23% i vleres mbi 150 000 leke + 13% e diferences mes vleres 150 000 leke dhe 30 000 leke.
	 */
	public FinanceDTO calculateFinancialData(ActivityAggregatorDTO activity, double tapLow, double tapMed, double tapMedPerc, double tapMax, 
		double tapMaxPerc, double minimalPay, double maximumPay, double healthInsuranceEmployer, double healthInsuranceEmployee, 
		double socialInsuranceEmployer, double socialInsuranceEmployee) throws NumberFormatException, Exception {
		
		FinanceDTO financeDto = new FinanceDTO();
		double grossSalary = activity.getGrossSalary() != null ? activity.getGrossSalary() : 0.0;
		
		// In case the gross salary is higher than the maximum pay, the health insurance is calculated using the max pay. Otherwise we use the gross salary
		double salaryForSocialInsurance = grossSalary > maximumPay ? maximumPay : grossSalary;
		
		double employerHealthInsurance = grossSalary * healthInsuranceEmployer;
		double employerSocialInsurance = salaryForSocialInsurance * socialInsuranceEmployer;
		
		double employeeHealthInsurance = grossSalary * healthInsuranceEmployee;
		double employeeSocialInsurance = salaryForSocialInsurance * socialInsuranceEmployee;
		
		double progressiveTax = 0.0;
		
		if (grossSalary >= tapLow && grossSalary <= tapMed) {
			progressiveTax = 0.0;
		} else if (grossSalary > tapMed && grossSalary <= tapMax) {
			progressiveTax = (grossSalary - tapMed) * tapMedPerc;
		} else if (grossSalary > tapMax) {
			progressiveTax = (grossSalary - tapMax) * tapMaxPerc + (tapMedPerc * (tapMax - tapMed));
		}
		
		double netSalary = grossSalary - employeeHealthInsurance - employeeSocialInsurance - progressiveTax;
		
		financeDto.setHealthInsuranceSalary(grossSalary);
		financeDto.setSocialInsuranceSalary(salaryForSocialInsurance);
		financeDto.setEmployerHealthInsurance(Utils.twoDecimalPlacesDouble(employerHealthInsurance));
		financeDto.setEmployerSocialInsurance(Utils.twoDecimalPlacesDouble(employerSocialInsurance));
		financeDto.setEmployeeHealthInsurance(Utils.twoDecimalPlacesDouble(employeeHealthInsurance));
		financeDto.setEmployeeSocialInsurance(Utils.twoDecimalPlacesDouble(employeeSocialInsurance));
		financeDto.setTotalEmploymentCost(Utils.twoDecimalPlacesDouble(grossSalary + employerHealthInsurance + employerSocialInsurance));
		financeDto.setProgressiveTax(Utils.twoDecimalPlacesDouble(progressiveTax));
		financeDto.setGrossSalary(Utils.twoDecimalPlacesDouble(grossSalary));
		financeDto.setNetSalary(Utils.twoDecimalPlacesDouble(netSalary));
		financeDto.setBelowMinimalWage(grossSalary < minimalPay ? true : false);
		
		EmployeeFinancialDTO employee = new EmployeeFinancialDTO();
		employee.toDTO(activity.getEmployee());
		financeDto.setEmployee(employee);
		
		return financeDto;
	}
	
	public Iterable<Integer> findYearsWithData() {
		return activityRepo.findYearsWithData();
	}
	
	public Iterable<DailyActivityDTO> findActivityByAreaAndDate(Area area, Date date) {
		return activityRepo.findActivityByAreaAndDate(area, date);
	}
	
	public Iterable<DailyActivityDetailDTO> findActivityByAreaAndDateAndEmployee(Area area, Date date, Employee employee) {
		return activityRepo.findActivityByAreaAndDateAndEmployee(area, date, employee);
	}
	
	public Iterable<Activity> findAllActivityByAreaAndDate(Area area, Date date) {
		return activityRepo.findAllActivityByAreaAndDate(area, date);
	}
}
