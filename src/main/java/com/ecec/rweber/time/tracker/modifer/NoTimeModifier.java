package com.ecec.rweber.time.tracker.modifer;

import com.ecec.rweber.time.tracker.ElapsedTimer;

public class NoTimeModifier implements TimeModifier {

	@Override
	public long modifyTime(ElapsedTimer t) {
		//do nothing to the time
		return t.getStopTime();
	}

}
