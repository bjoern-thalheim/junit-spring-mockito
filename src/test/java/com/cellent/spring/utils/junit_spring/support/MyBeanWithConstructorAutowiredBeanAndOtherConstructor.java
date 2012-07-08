package com.cellent.spring.utils.junit_spring.support;

import org.springframework.beans.factory.annotation.Autowired;

public class MyBeanWithConstructorAutowiredBeanAndOtherConstructor {
	
	private MyDelegate delegate;
	
	@Autowired
	public MyBeanWithConstructorAutowiredBeanAndOtherConstructor(MyDelegate theDelegate) {
		this.delegate = theDelegate;
	}
	
	public MyBeanWithConstructorAutowiredBeanAndOtherConstructor() {
		throw new RuntimeException("Please call the Autowired-Konstructor!");
	}
	
	public MyDelegate getDelegate() {
		return delegate;
	}

}
