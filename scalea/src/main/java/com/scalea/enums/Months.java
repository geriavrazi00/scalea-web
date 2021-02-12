package com.scalea.enums;

import com.scalea.configurations.Messages;

public enum Months {
	JANUARY(1, "messages.january", "January", "Janar"),
	FEBRUARY(2, "messages.february", "February", "Shkurt"),
	MARCH(3, "messages.march", "March", "Mars"),
	APRIL(4, "messages.april", "April", "Prill"),
	MAY(5, "messages.may", "May", "Maj"),
	JUNE(6, "messages.june", "June", "Qershor"),
	JULY(7, "messages.july", "July", "Korrik"),
	AUGUST(8, "messages.august", "August", "Gusht"),
	SEPTEMBER(9, "messages.september", "September", "Shtator"),
	OCTOBER(10, "messages.october", "October", "Tetor"),
	NOVEMBER(11, "messages.november", "November", "Nëntor"),
	DECEMBER(12, "messages.december", "December", "Dhjetor");
	
	private int number;
	private String nameKey;
	private String enName;
	private String sqName;
	
	private Months(int number, String nameKey, String enName, String sqName) {
		this.number = number;
		this.nameKey = nameKey;
		this.enName = enName;
		this.sqName = sqName;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public String getNameKey() {
		return nameKey;
	}

	public void setNameKey(String nameKey) {
		this.nameKey = nameKey;
	}
	
	public String getEnName() {
		return enName;
	}

	public void setEnName(String enName) {
		this.enName = enName;
	}

	public String getSqName() {
		return sqName;
	}

	public void setSqName(String sqName) {
		this.sqName = sqName;
	}

	public String getLocaleNameByNumber(int month, Messages messages) {
		if (month == Months.JANUARY.getNumber()) {
			return messages.get(Months.JANUARY.getNameKey());
		} else if (month == Months.FEBRUARY.getNumber()) {
			return messages.get(Months.FEBRUARY.getNameKey());
		} else if (month == Months.MARCH.getNumber()) {
			return messages.get(Months.MARCH.getNameKey());
		} else if (month == Months.APRIL.getNumber()) {
			return messages.get(Months.APRIL.getNameKey());
		} else if (month == Months.MAY.getNumber()) {
			return messages.get(Months.MAY.getNameKey());
		} else if (month == Months.JUNE.getNumber()) {
			return messages.get(Months.JUNE.getNameKey());
		} else if (month == Months.JULY.getNumber()) {
			return messages.get(Months.JULY.getNameKey());
		} else if (month == Months.AUGUST.getNumber()) {
			return messages.get(Months.AUGUST.getNameKey());
		} else if (month == Months.SEPTEMBER.getNumber()) {
			return messages.get(Months.SEPTEMBER.getNameKey());
		} else if (month == Months.OCTOBER.getNumber()) {
			return messages.get(Months.OCTOBER.getNameKey());
		} else if (month == Months.NOVEMBER.getNumber()) {
			return messages.get(Months.NOVEMBER.getNameKey());
		} else if (month == Months.DECEMBER.getNumber()) {
			return messages.get(Months.DECEMBER.getNameKey());
		} else {
			return null;
		}
	}
	
	public int getNumberByName(String name) {
		if (name.equals(Months.JANUARY.getSqName()) || name.equals(Months.JANUARY.getEnName())) {
			return Months.JANUARY.getNumber();
		} else if (name.equals(Months.FEBRUARY.getSqName()) || name.equals(Months.FEBRUARY.getEnName())) {
			return Months.FEBRUARY.getNumber();
		} else if (name.equals(Months.MARCH.getSqName()) || name.equals(Months.MARCH.getEnName())) {
			return Months.MARCH.getNumber();
		} else if (name.equals(Months.APRIL.getSqName()) || name.equals(Months.APRIL.getEnName())) {
			return Months.APRIL.getNumber();
		} else if (name.equals(Months.MAY.getSqName()) || name.equals(Months.MAY.getEnName())) {
			return Months.MAY.getNumber();
		} else if (name.equals(Months.JUNE.getSqName()) || name.equals(Months.JUNE.getEnName())) {
			return Months.JUNE.getNumber();
		} else if (name.equals(Months.JULY.getSqName()) || name.equals(Months.JULY.getEnName())) {
			return Months.JULY.getNumber();
		} else if (name.equals(Months.AUGUST.getSqName()) || name.equals(Months.AUGUST.getEnName())) {
			return Months.AUGUST.getNumber();
		} else if (name.equals(Months.SEPTEMBER.getSqName()) || name.equals(Months.SEPTEMBER.getEnName())) {
			return Months.SEPTEMBER.getNumber();
		} else if (name.equals(Months.OCTOBER.getSqName()) || name.equals(Months.OCTOBER.getEnName())) {
			return Months.OCTOBER.getNumber();
		} else if (name.equals(Months.NOVEMBER.getSqName()) || name.equals(Months.NOVEMBER.getEnName())) {
			return Months.NOVEMBER.getNumber();
		} else if (name.equals(Months.DECEMBER.getSqName()) || name.equals(Months.DECEMBER.getEnName())) {
			return Months.DECEMBER.getNumber();
		} else {
			return 0;
		}
	}
}
