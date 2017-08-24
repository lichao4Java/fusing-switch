伴随着微服务架构被宣传得如火如荼，一些概念也被推到了我们面前，其实大多数概念以前就有，但很少被提的这么频繁。

在股票市场，熔断这个词大家都不陌生，是指当股指波幅达到某个点后，交易所为控制风险采取的暂停交易措施。相应的，服务熔断一般是指软件系统中，由于某些原因使得服务出现了过载现象，为防止造成整个系统故障，从而采用的一种保护措施，所以很多地方把熔断亦称为过载保护。

这个框架其实就是要解决微服务架构中，服务于服务之间强依赖的一些问题，比如A服务依赖B服务，如果B服务挂了那么A服务会不正常，或者B服务响应很慢，A服务对外响应也很慢，也许只是B服务出现了问题，A服务是正常的，如果不加以处理，整个表现就是A和B都是不可服务的，且会有雪崩的危险。

针对上述情况，比较好的处理结果是，B出现了问题，A可以感知到并且断开依赖B的服务，A按照一种预设的规则进行处理，过一段时间B如果恢复正常了，A可以感知到并且重新连接上依赖B的服务，这种机制就是**服务熔断**机制。

很多人容易把 **服务熔断** 和 **服务降级** 弄混，其实这2者区别还是很大的：

**最终表现一样**：最终都是某些服务或功能不可用，提供有损服务

**触发条件不一样**：服务熔断一般是因为下游服务出现了问题而自动触发熔断开关，服务降级可以是人工触发（屏蔽降级）的，也可以是根据自身硬件、软件情况自动主动触发（策略降级）

**应用的范围不一样**：服务熔断一般是针对依赖的下游服务，只要存在接口调用就应该存在服务熔断机制，服务降级可以是整个服务降级，也可以是降级服务中的某些功能（比如 在做促销时对商品详情页的评论列表进行降级）


熔断机制的设计思路，把每个服务划分成3种状态：

**Closed**：熔断器关闭状态，记录调用失败次数积累，到了阈值则打开熔断开关

**Open**：熔断器打开状态，此时对下游的调用都内部直接返回错误，不走网络，但设计了一个时钟选项，默认的时钟达到了一定时间（这个时间一般设置成平均故障处理时间，也就是MTTR），到了这个时间，进入半熔断状态

**Half-Open**：半熔断状态，允许定量的服务请求，如果在一定的时间范围内成功次数达到阈值，则认为恢复了，关闭熔断器，否则认为还没好，又回到熔断器打开状态

下面上代码，提供了多种方式来给服务于服务之间增加熔断开关（保险丝）：

# **1 侵入式接入：**

  **1） 如果依赖的服务是RMI类的接口，比如hassion，dubbo。**
	
	FusingSwitchStrategy.call().execute(new AbstractFusingSwitchTarget() {
		
		@Override
		public Object execute() throws Throwable {
			//要调用的RPC接口
			return order.saveOrder("abc123");
		}
		
	}
	, 
	//order接口的服务名称，可直接用类名
	new AbstractFusingSwitchProvider("com.a.b.c.Order")
	, 
	//mock execute
	new AbstractFusingSwitchMock() {
		
		@Override
		public Object executeMock(AbstractFusingSwitchProvider provider)
				throws Exception {
			//saveOrder接口的mock实现
			//可以throw Exception，可以返回一个对象，可以调用orderMock的saveOrder方法
			return orderMock.saveOrder("abc123");
		}
	});


  **2）如果依赖的服务是HTTP协议的接口：**

	
	FusingSwitchStrategy.call().execute(new AbstractFusingSwitchTarget() {
		
		@Override
		public Object execute() throws Throwable {
			return sendHTTPRequest("http://orderservice.xxx.com/saveOrder", "abc123")
		}
		
	}
	, 
	//这块直接使用host作为服务名称
	new AbstractFusingSwitchProvider("orderservice.xxx.com")
	, 
	//mock execute
	new AbstractFusingSwitchMock() {
		
		@Override
		public Object executeMock(AbstractFusingSwitchProvider provider)
				throws Exception {
			//saveOrder接口的mock实现
			//可以throw Exception，可以返回一个对象，可以调用orderMock的saveOrder方法
			return orderMock.saveOrder("abc123");
		}
	})



# **2 非浸入式接入，使用拦截器（针对RMI协议类有效）**

   1） 配置interceptor

		<bean id="fusingSwitchRMIInterceptor" class="com.qding.fusing.ext.rmi.FusingSwitchRMIInterceptor" ></bean>
	<aop:config> 
	             <!--expression 是依赖的Order服务接口类--> 
	             <aop:pointcut id="fusingSwitchRMInterceptor" expression="execution(public * com.package.Order.*(..)) "/>  
	             <aop:advisor pointcut-ref="fusingSwitchRMInterceptor" advice-ref="fusingSwitchRMIInterceptor"/>
	</aop:config> 

   2）配置服务的Mock

      1）新建mock.properties文件

	com.package.Order=com.my.OrderMock

      2）spring中新增配置

	<bean id="mockConfig" class="com.qding.fusing.ext.rmi.mock.FusingSwitchRMIMockConfig">
		    <constructor-arg>
		        <value>mock.properties</value>
		    </constructor-arg>
	</bean>


##  **推荐使用拦截器的接入方式，无代码侵入，只需要配置拦截器和mock。**

# **3 参数配置**

    1）新建fusing-switch.properties文件

	#close状态下失败次数达到了开启熔断的条件
	open_fusing_switch_faild_count=10
	#close状态下在指定的时间内失败次数达到了开启熔断的条件
	open_fusing_switch_faild_second=120
	#open状态持续时间
	open_fusing_switch_second=60
	#half-open状态下指定的时间内成功次数到达了关闭熔断开关的条件
	half_open_fusing_switch_success_second=300
	#half-open状态下成功次数达到了关闭熔断开关的条件
	half_open_fusing_switch_success_count=5

    2）spring中新增配置

	<bean id="fusingSwitchConfig" class="com.qding.fusing.FusingSwitchConfig">
	    <constructor-arg>
	        <value>fusing-switch.properties</value>
	    </constructor-arg>
	</bean>
