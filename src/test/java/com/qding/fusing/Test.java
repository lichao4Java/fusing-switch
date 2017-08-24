package com.qding.fusing;

import org.apache.log4j.Logger;

import com.qding.fusing.abstracts.AbstractFusingSwitchMock;
import com.qding.fusing.abstracts.AbstractFusingSwitchProvider;
import com.qding.fusing.abstracts.AbstractFusingSwitchTarget;

public class Test {

	public static void main(String[] args) throws Throwable {

		Logger l = Logger.getLogger("Test");
		
		final long s = System.currentTimeMillis();
		
		for(;;) {
			
		Thread.sleep(100l);
//		new Thread(new Runnable() {
//			
//			public void run() {
				try {
					
//					System.out.println(
//							
//					FusingSwitchStrategy.call().execute(new AbstractFusingSwitchTarget() {
//						
//						@Override
//						public Object execute() throws Throwable {
//							//send http request has error!!!
//							if(System.currentTimeMillis() - s > 2 * 60 * 1000l) {
//								return "{'name' : 'LICHAO'}";
//							}
//							throw new Exception("ERROR! request timed out");
//						}
//						
//					}
//					, 
//					//http service provider is 'www.baidu.com'
//					new AbstractFusingSwitchProvider("www.baidu.com")
//					, 
//					//mock execute
//					new AbstractFusingSwitchMock() {
//						
//						@Override
//						public Object executeMock(AbstractFusingSwitchProvider provider)
//								throws Exception {
//							//return mock data
//							return "{'name' : 'mock data'}";
//						}
//					}));
					
					
					
					
					l.info(
							
					FusingSwitchStrategy.call().execute(new AbstractFusingSwitchTarget() {
						
						@Override
						public Object execute() throws Throwable {
							//send rpc request
							//return order.getOrder("abc123");
							
							if(System.currentTimeMillis() - s > 15 * 1000l) {
								
								return "{'name' : 'LICHAO'}";
							}
							
							throw new Exception("ERROR22222!!!!");
						}
						
					}
					, 
					//http service provider is 'core-order-system'
					new AbstractFusingSwitchProvider("core-order-system")
					, 
					//mock execute
					new AbstractFusingSwitchMock() {
						
						@Override
						public Object executeMock(AbstractFusingSwitchProvider provider)
								throws Exception {
							//1 return mock data
							return "{'name' : 'mock data'}";
							
							//2 or return execute mock method
							//return orderMock.getOrder("abc123");
						}
					})
					
							);
					
					
				} catch (Throwable e) {
					e.printStackTrace();
				}
			}
//		}).start();
		
//		} 
	}
	
	
}
