package com.ecec.rweber.time.tracker;

import java.util.Observable;

import com.ecec.rweber.time.tracker.util.TimeFormatter;

public class CountdownTimer extends Observable implements Timer  {
	private TimerState current_state = TimerState.IDLE;
	private CountdownThread countdown = null;
	private double countdown_amount = 0;
	
	public CountdownTimer(){
		
	}
	
	public void setAmount(int amount, int format){
		//convert to milliseconds
		countdown_amount = TimeFormatter.format(amount, format, TimeFormatter.MILLISECONDS);
	}
	
	@Override
	public void start() {
		if(current_state == TimerState.RUNNING)
		{
			//stop the current thread
			countdown.stopCountdown();
		}

		//start a new one
		countdown = new CountdownThread(countdown_amount);
		
		Thread t = new Thread(countdown);
		t.start();
		
		current_state = TimerState.RUNNING;
	}

	@Override
	public void stop() {
		if(current_state == TimerState.RUNNING)
		{
			countdown.stopCountdown();
		}
		
		current_state = TimerState.STOPPED;
	}

	@Override
	public void reset() {
		this.stop();

		current_state = TimerState.IDLE;
	}

	@Override
	public TimerState getState() {
		return current_state;
	}

	@Override
	public String toString(){
		String result = "";
		
		//can only report if running
		if(current_state == TimerState.RUNNING)
		{
			double timeLeft = countdown.timeLeft();
		
			//figure out the best format for this time 
			int bestFormat = TimeFormatter.guessBestFormat((long)timeLeft,TimeFormatter.MILLISECONDS);
		
			result = TimeFormatter.format((long)timeLeft, TimeFormatter.MILLISECONDS, bestFormat) + " " + TimeFormatter.toString(bestFormat);
		}
		
		return result;
	}
	
	private class CountdownThread implements Runnable {
		private boolean keepRunning = true;
		private double amountLeft = 0;
		
		public CountdownThread(double amount){
			amountLeft = amount;
		}

		public void stopCountdown(){
			keepRunning = false;
		}
		
		public double timeLeft(){
			return amountLeft;
		}
		
		@Override
		public void run() {
			
			while(keepRunning){
				try{
					//subtract 100 milliseconds
					amountLeft = amountLeft - 100;
					
					Thread.sleep(100);
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
				
				if(amountLeft <= 0){
					keepRunning = false;
				}
			}
			
			//we're now done
			current_state = TimerState.STOPPED;
			
			//only notify if we completed the countdown
			if(amountLeft <= 0)
			{
				setChanged();
				notifyObservers(countdown_amount);
			}
		}
	}
}
