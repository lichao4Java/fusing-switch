package com.qding.fusing.ext.rmi.mock;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.core.io.ClassPathResource;

public class FusingSwitchRMIMockConfig {

	private static Logger logger = Logger.getLogger(FusingSwitchRMIMockConfig.class);
	
	private static Map<Class<?>, Class<?>> value = new HashMap<Class<?>, Class<?>>();
	
	public void init(String config_path) throws Exception {
		
		logger.info("load properties " + config_path);

        ClassPathResource cp = new ClassPathResource("/" + config_path);
        
        if (cp.exists()) {
        	Properties ps = new Properties();
            ps.load(cp.getInputStream());
            
            Set<Entry<Object, Object>> entrySet = ps.entrySet();
            for(Map.Entry<Object, Object> entry: entrySet){
                String provider = entry.getKey().toString();
                String providerMock = entry.getValue().toString();
                
                try {
                	Class<?> providerClass = Class.forName(provider);
                	Class<?> providerMockClass = Class.forName(providerMock);

                	if(!providerMockClass.isAssignableFrom(providerClass)) {
                		logger.error("providerMockClass is not assignableFrom " + providerClass);
                	}
                	value.put(providerClass, providerMockClass);

                } catch(Exception e) {
                	e.printStackTrace();
                }
                
            }
            
        }
        else {
        	logger.error(config_path + " not exists");
        }
	}
	
	public static Class<?> getMock(Class<?> provider) {
		return value.get(provider);
	}
}
