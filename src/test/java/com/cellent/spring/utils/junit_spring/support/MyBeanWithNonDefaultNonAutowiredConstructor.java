package com.cellent.spring.utils.junit_spring.support;

public class MyBeanWithNonDefaultNonAutowiredConstructor {

	private final MyDelegate delegate;

	public MyBeanWithNonDefaultNonAutowiredConstructor(MyDelegate theDelegate) {
		this.delegate = theDelegate;
	}

	public MyDelegate getDelegate() {
		return delegate;
	}

}
