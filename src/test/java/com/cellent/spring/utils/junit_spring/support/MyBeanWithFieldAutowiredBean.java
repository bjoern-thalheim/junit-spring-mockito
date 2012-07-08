package com.cellent.spring.utils.junit_spring.support;

import org.springframework.beans.factory.annotation.Autowired;

public class MyBeanWithFieldAutowiredBean {
	
	@Autowired
	private MyDelegate delegate;
	
	public MyDelegate getDelegate() {
		return delegate;
	}

}
