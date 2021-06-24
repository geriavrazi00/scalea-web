package com.scalea.models.dto;

import java.util.Date;

import com.scalea.utils.Utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DailyActivityDetailDTO {
	private String employeeFirstName;
	private String employeeLastName;
	private int vacancyNumber;
	private String productName;
	private double weight;
	private Date date;
	
	public double getWeight() {
		return Utils.twoDecimalPlacesDouble(this.weight);	
	}
	
	public String getTime() {
		return Utils.getTimeFromDate(this.date);
	}
}
