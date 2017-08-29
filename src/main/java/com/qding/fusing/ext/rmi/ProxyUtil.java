package com.qding.fusing.ext.rmi;

import java.lang.reflect.Field;

import org.springframework.aop.framework.AdvisedSupport;
import org.springframework.aop.framework.AopProxy;
import org.springframework.aop.support.AopUtils;

public class ProxyUtil {

	//get Cglib proxy target
	public static Class<?> getCglibProxyTargetClass(Object proxy) throws Exception {  
        Field h = proxy.getClass().getDeclaredField("CGLIB$CALLBACK_0");  
        h.setAccessible(true);  
        Object dynamicAdvisedInterceptor = h.get(proxy);  
        Field advised = dynamicAdvisedInterceptor.getClass().getDeclaredField("advised");  
        advised.setAccessible(true);  
        Object target = ((AdvisedSupport)advised.get(dynamicAdvisedInterceptor)).getTargetSource().getTarget();  
        return target.getClass();  
    }  
  
  
	//get jdk proxy target
	public static Class<?> getJdkDynamicProxyTargetClass(Object proxy) throws Exception {  
        Field h = proxy.getClass().getSuperclass().getDeclaredField("h");  
        h.setAccessible(true);  
        AopProxy aopProxy = (AopProxy) h.get(proxy);  
        Field advised = aopProxy.getClass().getDeclaredField("advised");  
        advised.setAccessible(true);  
        Object target = ((AdvisedSupport)advised.get(aopProxy)).getTargetSource().getTarget();  
        return target.getClass();  
    }  
    
    
	//get dubbo javassist proxy target
	public static Class<?> getDubboJavassistProxyTargetClass(Object proxy) {
    	Class<?>[] interfaces = proxy.getClass().getInterfaces();
    	for(int i = 0; i < interfaces.length; i ++) {
    		if(interfaces[i].getSimpleName().equals("EchoService")) {
    			continue;
    		}
    		if(interfaces[i].getSimpleName().equals("DC")) {
    			continue;
    		}
    		return interfaces[i];
    	}
    	return null;
    }
    
	//is dubbo javassist proxy
	public static boolean isDubboJavassistProxy(Object proxy) {
    	Class<?>[] interfaces = proxy.getClass().getInterfaces();
    	for(int i = 0; i < interfaces.length; i ++) {
    		if(interfaces[i].getSimpleName().equals("EchoService")) {
    			return true;
    		}
    	}
    	return false;
    }
	
	//is cglib proxy
	public static boolean isCglibProxy(Object proxy) {
		return AopUtils.isCglibProxy(proxy);
	}
	
	//is jdk proxy
	public static boolean isJdkDynamicProxy(Object proxy) {
		return AopUtils.isJdkDynamicProxy(proxy);
	}
}
