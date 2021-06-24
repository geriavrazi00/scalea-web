package com.scalea.models.dto;

import com.scalea.entities.Employee;
import com.scalea.utils.Utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DailyActivityDTO {
	private long weighings;
	private double totalWeight;
	private Employee employee;
	
	public double getTotalWeight() {
		return Utils.twoDecimalPlacesDouble(this.totalWeight);	
	}
}
