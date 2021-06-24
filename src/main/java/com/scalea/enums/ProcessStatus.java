package com.scalea.enums;

public enum ProcessStatus {
	STARTED(0),
	PAUSED(1),
	FINISHED(2);
	
	private int status;
	
	ProcessStatus(int status) {
		this.status = status;
	}

	public int getStatus() {
		return status;
	}
	
	public static String getStatusTranslationMessage(int status) {
		switch(status) {
			case 0:
				return "messages.active";
			case 1:
				return "messages.paused";
			case 2: 
				return "messages.finished";
			default:
				return null;
		}
	}
}
