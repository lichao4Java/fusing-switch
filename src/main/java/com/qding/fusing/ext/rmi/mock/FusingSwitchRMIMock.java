package com.qding.fusing.ext.rmi.mock;

import com.qding.fusing.abstracts.AbstractFusingSwitchMock;
import com.qding.fusing.abstracts.AbstractFusingSwitchProvider;
import com.qding.fusing.ext.rmi.FusingSwitchRMIProvider;


public abstract class FusingSwitchRMIMock extends AbstractFusingSwitchMock{

	@Override
	public boolean isSupportMock(AbstractFusingSwitchProvider provider) throws Exception{
		
		FusingSwitchRMIProvider pro = (FusingSwitchRMIProvider) provider;
		
		return FusingSwitchRMIMockConfig.getMock(pro.getClazz()) != null;
	}
	
}
