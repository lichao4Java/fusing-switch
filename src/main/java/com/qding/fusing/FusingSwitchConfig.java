package com.qding.fusing;

import java.util.Properties;

import org.apache.log4j.Logger;
import org.springframework.core.io.ClassPathResource;

/**
 * 
 * @author lichao
 *
 */
public class FusingSwitchConfig {

	//close状态下失败次数达到了开启熔断的条件
	public static int openFusingSwitchFaildCount = 3;
	
	//close状态下在指定的时间内失败次数达到了开启熔断的条件
	public static int openFusingSwitchFaildSecond = 30;

	//open状态持续时间
	public static int openFusingSwitchSecond = 5;
	
	//half-open状态下指定的时间内成功次数到达了关闭熔断开关的条件
	public static int halfOpenFusingSwitchSuccessSecond = 5;
	
	//half-open状态下成功次数达到了关闭熔断开关的条件
	public static int halfOpenFusingSwitchSuccessCount = 5;
	
	public static boolean alwaysUseMock;

	private static Logger logger = Logger.getLogger(FusingSwitchConfig.class);
	
	public FusingSwitchConfig(String config_path) throws Exception {
		
		logger.info("load properties " + config_path);

        ClassPathResource cp = new ClassPathResource("/" + config_path);
        
        if (cp.exists()) {
        	Properties ps = new Properties();
            ps.load(cp.getInputStream());
            openFusingSwitchFaildCount = Integer.parseInt(ps.getProperty("open_fusing_switch_faild_count", "10"));
            openFusingSwitchFaildSecond = Integer.parseInt(ps.getProperty("open_fusing_switch_faild_second", "120"));
            openFusingSwitchSecond =  Integer.parseInt(ps.getProperty("open_fusing_switch_second", "60"));
            halfOpenFusingSwitchSuccessSecond = Integer.parseInt(ps.getProperty("half_open_fusing_switch_success_second", "300"));
            halfOpenFusingSwitchSuccessCount = Integer.parseInt(ps.getProperty("half_open_fusing_switch_success_count", "5"));
            alwaysUseMock = Boolean.parseBoolean(ps.getProperty("always_use_mock", "false"));
        }
        else {
        	logger.error(config_path + " not exists");
        }
	}
}
