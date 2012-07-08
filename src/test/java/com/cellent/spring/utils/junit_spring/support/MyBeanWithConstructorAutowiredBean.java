package com.cellent.spring.utils.junit_spring.support;

import org.springframework.beans.factory.annotation.Autowired;

public class MyBeanWithConstructorAutowiredBean {
	
	private final MyDelegate delegate;
	
	@Autowired
	public MyBeanWithConstructorAutowiredBean(MyDelegate theDelegate) {
		this.delegate = theDelegate;
	}
	
	public MyDelegate getDelegate() {
		return delegate;
	}

}
