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
}
