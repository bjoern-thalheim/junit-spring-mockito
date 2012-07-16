package com.cellent.spring.utils.junit_spring.support;

import org.springframework.context.ApplicationContext;

public class MyBeanGettingDelegateFromApplicationContextAwareByName {
	
	public Object getDelegateInstance() {
		ApplicationContext applicationContext = MyApplicationContextHolder.getApplicationContext();
		return applicationContext.getBean("weirdNameForBean");
	}

}
