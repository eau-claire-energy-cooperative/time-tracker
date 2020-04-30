package com.ecec.rweber.time.tracker.upgrade;

public class DatabaseUpgradeException extends Exception {
	private static final long serialVersionUID = 1L;

	@Override
	public String getMessage() {
		return "Error upgrading database";
	}
}
