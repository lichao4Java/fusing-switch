package com.qding.fusing;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.qding.fusing.abstracts.AbstractFusingSwitchProvider;

/**
 * 
 * @author lichao
 *
 */
public class FusingSwitchStatus {

	private Map<AbstractFusingSwitchProvider, FusingSwitchValue> value = new HashMap<AbstractFusingSwitchProvider, FusingSwitchValue>();
	
	private static Logger logger = Logger.getLogger(FusingSwitchStatus.class);

	public synchronized FusingSwitchValue getFusingSwitchStatus(AbstractFusingSwitchProvider provider) {
		
		FusingSwitchValue fusingValue = value.get(provider);
		
		if(fusingValue == null) {
			
			fusingValue = new FusingSwitchValue();
			
			value.put(provider, fusingValue);
			
			logger.info("provider " + provider.toString() + " , value " + fusingValue.toString() + " has inited");

		}
		
		return fusingValue;
	}
	
	/**
	 * 打开开关
	 * @param provider
	 */
	public void openFusingSwitchStatus(final AbstractFusingSwitchProvider provider) {
		
		FusingSwitchValue fusingValue = getFusingSwitchStatus(provider);
		
		synchronized (provider) {
			fusingValue.setOpenFusingSwitchSecondExpiredAt(System.currentTimeMillis() + FusingSwitchConfig.openFusingSwitchSecond * 1000L);
			fusingValue.setFusingSwitchStatus(1);
			logger.info("provider " + provider.toString() + " , value " + fusingValue.toString() + " fusingSwitch has opened");

		}
	}
	
	/**
	 * 关闭开关
	 * @param provider
	 */
	public void closeFusingSwitchStatus(AbstractFusingSwitchProvider provider) {

		FusingSwitchValue fusingValue = getFusingSwitchStatus(provider);
		
		synchronized (provider) {
			fusingValue.getOpenFusingSwitchFaildCount().set(0);
			fusingValue.setOpenFusingSwitchFaildSecondExpireAt(0l);
			fusingValue.setFusingSwitchStatus(0);
			logger.info("provider " + provider.toString() + " , value " + fusingValue.toString() + " fusingSwitch has closed");
		}
	}
	
	/**
	 * 打开半开关
	 * @param provider
	 */
	public void halfOpenFusingSwitchStatus(AbstractFusingSwitchProvider provider) {

		FusingSwitchValue fusingValue = getFusingSwitchStatus(provider);
		
		synchronized (provider) {
			fusingValue.getHalfOpenFusingSwitchSuccessCount().set(0);
			fusingValue.setHalfOpenFusingSwitchSuccessSecondExpireAt(0l);
			fusingValue.setOpenFusingSwitchSecondExpiredAt(0l);
			fusingValue.setFusingSwitchStatus(2);
			
			logger.info("provider " + provider.toString() + " , value " + fusingValue.toString() + " has half-opened");

		}
	}
}
