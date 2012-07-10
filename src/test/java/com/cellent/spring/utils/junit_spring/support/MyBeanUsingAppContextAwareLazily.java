package com.cellent.spring.utils.junit_spring.support;

import org.springframework.context.ApplicationContextAware;

/**
 * Test class for lazy init of a bean via {@link ApplicationContextAware}.
 * 
 * @author bjoern
 * 
 */
public class MyBeanUsingAppContextAwareLazily {

	/**
	 * Lazy initialized delegate.
	 */
	private MyDelegate myDelegate;

	/**
	 * Lazy init {@link #myDelegate} and return it.
	 * 
	 * @return {@link #myDelegate}.
	 */
	public MyDelegate getMyDelegate() {
		if (myDelegate == null) {
			myDelegate = MyApplicationContextHolder.getMyDelegate();
		}
		return myDelegate;
	}

}
