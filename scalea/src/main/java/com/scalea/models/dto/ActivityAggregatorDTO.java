package com.scalea.models.dto;

import com.scalea.entities.Employee;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActivityAggregatorDTO {
	private Double grossSalary;
	private Employee employee;
}
