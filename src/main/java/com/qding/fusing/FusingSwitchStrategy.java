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
				long faildCountExpireAt = fusingValue.getOpenFusingSwitchFaildSecondExpireAt();
				
				//时间范围外的失败数量，进行重置
				if(faildCountExpireAt < System.currentTimeMillis()) {
					logger.info("provider " + provider.toString() + " , value " + fusingValue.toString() + " openFusingSwitchFaildSecond has expired");
					//reset
					fusingValue.getOpenFusingSwitchFaildCount().set(0);
					fusingValue.setOpenFusingSwitchFaildSecondExpireAt(
							System.currentTimeMillis() + FusingSwitchConfig.openFusingSwitchFaildSecond * 1000l
						);
				}
				
				if(fusingValue.getOpenFusingSwitchFaildCount().incrementAndGet() >= FusingSwitchConfig.openFusingSwitchFaildCount) {
					
					fusingstatus.openFusingSwitchStatus(provider);
					
				}
				
				throw e;
			}

		
		//open
		case 1:
			
			try {
				return mock.executeMock(provider);
			} 
			catch(InvocationTargetException e1) {
				throw e1.getTargetException();
			}
			catch (Exception e) {
				throw e;
			}
			
		//half-open
		case 2:
			
			try {
				
				//50% rate
				int[] rate = new int[]{1,3,5,7,9,2,4,6,8,10};
				
				int r = new Random().nextInt(10);
				
				if(rate[r] % 2 == 0) {
					
					Object object = target.execute();
					 
					//expire at
					long successCountExpireAt = fusingValue.getHalfOpenFusingSwitchSuccessSecondExpireAt();
					
					if(successCountExpireAt < System.currentTimeMillis()) {

						logger.info("provider " + provider.toString() + " , value " + fusingValue.toString() + " halfOpenFusingSwitchSuccessSecond has expired");

						//0 is default
						if(successCountExpireAt != 0) {
							
							//时间范围外 成功次数小于阀值
							if(fusingValue.getHalfOpenFusingSwitchSuccessCount().get() < FusingSwitchConfig.halfOpenFusingSwitchSuccessCount) {
								
								fusingstatus.openFusingSwitchStatus(provider);
								
								return object;
							}
						}
						
						//时间范围外的成功数量，进行重置
						fusingValue.getHalfOpenFusingSwitchSuccessCount().set(0);
						fusingValue.setHalfOpenFusingSwitchSuccessSecondExpireAt(
								System.currentTimeMillis() + FusingSwitchConfig.halfOpenFusingSwitchSuccessSecond * 1000l
							);
						
					}
					
					//时间范围内 成功次数大于阀值
					if(fusingValue.getHalfOpenFusingSwitchSuccessCount().incrementAndGet() >= FusingSwitchConfig.halfOpenFusingSwitchSuccessCount) {
						
						fusingstatus.closeFusingSwitchStatus(provider);
					}
					
					return object;
				}
				
				//return mock
				
				try {
					return mock.executeMock(provider);
				} 
				catch(InvocationTargetException e1) {
					throw e1.getTargetException();
				}
				catch (Exception e) {
					throw e;
				}
				
			} catch (Exception e) {
				throw e;
			}
		default:
			break;
		}

		return null;
	}
	
}
