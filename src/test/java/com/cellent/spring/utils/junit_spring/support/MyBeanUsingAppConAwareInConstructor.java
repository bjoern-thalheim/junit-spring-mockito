package com.cellent.spring.utils.junit_spring.support;

import org.springframework.context.ApplicationContextAware;

/**
 * Bean which uses an {@link ApplicationContextAware}-Class in the constructor
 * to instantiate a field.
 * 
 * @author bjoern
 */
public class MyBeanUsingAppConAwareInConstructor {

	private final MyDelegate delegate;

	public MyBeanUsingAppConAwareInConstructor() {
		delegate = MyApplicationContextHolder.getMyDelegate();
	}

	public MyDelegate getDelegate() {
		return delegate;
	}
}
