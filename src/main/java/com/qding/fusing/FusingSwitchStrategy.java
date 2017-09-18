package com.qding.fusing;

import java.lang.reflect.InvocationTargetException;
import java.util.Random;

import org.apache.log4j.Logger;

import com.qding.fusing.abstracts.AbstractFusingSwitchMock;
import com.qding.fusing.abstracts.AbstractFusingSwitchProvider;
import com.qding.fusing.abstracts.AbstractFusingSwitchTarget;

/**
 * 
 * @author lichao
 *
 */
public class FusingSwitchStrategy {

	private FusingSwitchStatus fusingstatus = new FusingSwitchStatus();
	
	private static Logger logger = Logger.getLogger(FusingSwitchStrategy.class);
	
	private static FusingSwitchStrategy self = new FusingSwitchStrategy();
	
	public static FusingSwitchStrategy call() {
		return self;
	}
	
	private FusingSwitchStrategy() {}
	
	public Object execute(AbstractFusingSwitchTarget target, AbstractFusingSwitchProvider provider, AbstractFusingSwitchMock mock) throws Throwable {

		if(!mock.isSupportMock(provider)) {
			return target.execute();
		}
		
		FusingSwitchValue fusingValue = fusingstatus.getFusingSwitchStatus(provider);

		//open状态被动过期到half-open状态
		if(fusingValue.getFusingSwitchStatus() == 1 && System.currentTimeMillis() > fusingValue.getOpenFusingSwitchSecondExpiredAt()) {
			
			fusingstatus.halfOpenFusingSwitchStatus(provider);
		}
		
		logger.info("provider " + provider.toString() + " , fusingstatus " + fusingValue.getFusingSwitchStatus());

		switch (fusingValue.getFusingSwitchStatus()) {
		
		//close
		case 0:
			try {
				return target.execute();
			} catch (Exception e) {
				//expire at
				long openFusingSwitchFaildSecondExpireAt = fusingValue.getOpenFusingSwitchFaildSecondExpireAt();
				long currentAt = System.currentTimeMillis();
				
				//时间范围外的失败数量，进行重置
				if(openFusingSwitchFaildSecondExpireAt < currentAt) {
					logger.info("provider " + provider.toString() + " , value " + fusingValue.toString() + " openFusingSwitchFaildSecond has expired");
					//reset
					fusingValue.getOpenFusingSwitchFaildCount().set(0);
					fusingValue.setOpenFusingSwitchFaildSecondExpireAt(
							currentAt + FusingSwitchConfig.openFusingSwitchFaildSecond * 1000l
						);
				}
				
				if(fusingValue.getOpenFusingSwitchFaildCount().incrementAndGet() >= FusingSwitchConfig.openFusingSwitchFaildCount) {
					
					fusingstatus.openFusingSwitchStatus(provider);
					
				}
				
				if(FusingSwitchConfig.alwaysUseMock) {
					logger.info("always_use_mock is open");
					logger.error(e.getMessage(), e);
					return executeMock(provider, mock);
				}
				else {
					throw e;
				}
			}

		
		//open
		case 1:
			
			return executeMock(provider, mock);
			
		//half-open
		case 2:
				
			//70% rate
			int[] rate = new int[]{1,3,5,2,4,6,8,10,12,14};
			
			int r = new Random().nextInt(10);
			
			if(rate[r] % 2 == 0) {
				
				try {
					Object object = target.execute();
					
					//expire at
					long halfOpenFusingSwitchSuccessSecondExpireAt = fusingValue.getHalfOpenFusingSwitchSuccessSecondExpireAt();
					long currentAt = System.currentTimeMillis();
					
					if(halfOpenFusingSwitchSuccessSecondExpireAt < currentAt) {

						logger.info("provider " + provider.toString() + " , value " + fusingValue.toString() + " halfOpenFusingSwitchSuccessSecond has expired");

						//0 is default
						if(halfOpenFusingSwitchSuccessSecondExpireAt != 0) {
							
							//时间范围外 成功次数小于阀值
							if(fusingValue.getHalfOpenFusingSwitchSuccessCount().get() < FusingSwitchConfig.halfOpenFusingSwitchSuccessCount) {
								
								fusingstatus.openFusingSwitchStatus(provider);
								
								return object;
							}
						}
						
						//时间范围外的成功数量，进行重置
						fusingValue.getHalfOpenFusingSwitchSuccessCount().set(0);
						fusingValue.setHalfOpenFusingSwitchSuccessSecondExpireAt(
								currentAt + FusingSwitchConfig.halfOpenFusingSwitchSuccessSecond * 1000l
							);
						
					}
					
					//时间范围内 成功次数大于阀值
					if(fusingValue.getHalfOpenFusingSwitchSuccessCount().incrementAndGet() >= FusingSwitchConfig.halfOpenFusingSwitchSuccessCount) {
						
						fusingstatus.closeFusingSwitchStatus(provider);
					}
					
					return object;
					
				} catch (Exception e) {
					
					if(FusingSwitchConfig.alwaysUseMock) {
						logger.info("always_use_mock is open");
						logger.error(e.getMessage(), e);
						return executeMock(provider, mock);
					}
					else {
						throw e;
					}
				}
			}
				
			//return mock
			return executeMock(provider, mock);
				
			
		default:
			break;
		}

		return null;
	}

	private Object executeMock(AbstractFusingSwitchProvider provider,
			AbstractFusingSwitchMock mock) throws Throwable {
		
		try {
			return mock.executeMock(provider);
		} 
		catch(InvocationTargetException e1) {
			throw e1.getTargetException();
		}
		catch (Exception e) {
			throw e;
		}
	}
	
}
