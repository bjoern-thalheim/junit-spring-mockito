package com.cellent.spring.utils.junit_spring.support;

import org.springframework.beans.factory.annotation.Autowired;

public class MyBeanWithSetterAutowiredBean {
	
	private MyDelegate delegate;
	
	@Autowired
	public void setDelegate(MyDelegate delegate) {
		this.delegate = delegate;
	}
	
	public MyDelegate getDelegate() {
		return delegate;
	}

}
