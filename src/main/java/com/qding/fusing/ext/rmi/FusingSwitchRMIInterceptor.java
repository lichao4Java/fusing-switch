package com.qding.fusing.ext.rmi;

import java.lang.reflect.Method;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import com.qding.fusing.FusingSwitchStrategy;
import com.qding.fusing.abstracts.AbstractFusingSwitchMock;
import com.qding.fusing.abstracts.AbstractFusingSwitchProvider;
import com.qding.fusing.abstracts.AbstractFusingSwitchTarget;
import com.qding.fusing.ext.rmi.mock.FusingSwitchRMIMock;
import com.qding.fusing.ext.rmi.mock.FusingSwitchRMIMockConfig;


/**
 * 为RMI类的服务接口提供的熔断拦截器
 * @author lichao
 *
 */
public class FusingSwitchRMIInterceptor implements MethodInterceptor {

	public Object invoke(final MethodInvocation invocation) throws Throwable {
		
		FusingSwitchRMIProvider provider = new FusingSwitchRMIProvider();
		
		Object providerTarget = invocation.getThis();
		
		Class<?> providerClazz  = getTargetClass(providerTarget);
		
		provider.setClazz(providerClazz);
		
		AbstractFusingSwitchMock mock = new FusingSwitchRMIMock() {
			
			@Override
			public Object executeMock(AbstractFusingSwitchProvider provider) throws Exception{
				
				FusingSwitchRMIProvider pro = (FusingSwitchRMIProvider) provider;
				Class<?> mockClazz = FusingSwitchRMIMockConfig.getMock(pro.getClazz());
				Object instance = mockClazz.newInstance();
				Method target = mockClazz.getMethod(invocation.getMethod().getName(), invocation.getMethod().getParameterTypes());
				return target.invoke(instance, invocation.getArguments());
			}
		};
		
		return FusingSwitchStrategy.call().execute(
				
			new AbstractFusingSwitchTarget(){

				@Override
				public Object execute() throws Throwable{
					return invocation.proceed();
			}}, provider, mock);
		
	}

	private Class<?> getTargetClass(Object object) throws Exception {
		Class<?> providerClazz;
		if(ProxyUtil.isCglibProxy(object)) {
			providerClazz = ProxyUtil.getCglibProxyTargetClass(object);
		}
		else if(ProxyUtil.isJdkDynamicProxy(object)) {
			providerClazz = ProxyUtil.getJdkDynamicProxyTargetClass(object);
		} 
		else if(ProxyUtil.isDubboJavassistProxy(object)) {
			providerClazz = ProxyUtil.getDubboJavassistProxyTargetClass(object);
		}
		else {
			providerClazz = object.getClass();
		}
		return providerClazz;
	}
    
}
