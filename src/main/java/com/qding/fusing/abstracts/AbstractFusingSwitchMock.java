package com.qding.fusing.abstracts;


public abstract class AbstractFusingSwitchMock {
	
	public boolean isSupportMock(AbstractFusingSwitchProvider provider) throws Exception {
		return true;
	}
	
	public abstract Object executeMock(AbstractFusingSwitchProvider provider) throws Exception;
	
	
}
