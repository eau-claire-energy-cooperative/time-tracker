package com.ecec.rweber.time.tracker.modifer;

public final class TimeModifierFactory {

	public static TimeModifier createModifier(Integer minTime, Integer roundTime) {
		TimeModifier result = null;
		
		//if one of these isn't 0
		if(minTime != 0 || roundTime != 0)
		{
			result = new MinimumsTimeModifier(minTime, roundTime);
		}
		else
		{
			result = TimeModifierFactory.createDefaultModifier();
		}
		
		return result;
	}
	
	public static TimeModifier createDefaultModifier() {
		return new NoTimeModifier();
	}
}
