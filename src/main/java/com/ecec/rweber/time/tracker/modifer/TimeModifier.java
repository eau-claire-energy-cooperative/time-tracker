package com.ecec.rweber.time.tracker.modifer;

import com.ecec.rweber.time.tracker.ElapsedTimer;

public interface TimeModifier {

	public long modifyTime(ElapsedTimer t);
}
