package com.ecec.rweber.time.tracker;

public interface Timer {

	public void start();
	public void stop();
	public void reset();
	public TimerState getState();
}
