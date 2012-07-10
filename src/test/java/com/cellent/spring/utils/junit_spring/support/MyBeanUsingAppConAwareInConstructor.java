package com.cellent.spring.utils.junit_spring.support;

public class MyBeanUsingAppConAwareInConstructor {
	
	private final MyDelegate delegate;

	public MyBeanUsingAppConAwareInConstructor() {
		delegate = MyApplicationContextHolder.getMyDelegate();
	}

	public MyDelegate getDelegate() {
		return delegate;
	}
}
