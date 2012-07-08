package com.cellent.spring.utils.junit_spring.support;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

public class MyInitializingBean implements InitializingBean {
	
	@Autowired
	private MyDelegate delegate;
	
	public MyDelegate getDelegate() {
		return delegate;
	}

	public void afterPropertiesSet() throws Exception {
		delegate.executeVoidCall();
	}

}
