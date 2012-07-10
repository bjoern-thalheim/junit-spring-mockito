package com.cellent.spring.utils.junit_spring.support;

import org.springframework.beans.factory.annotation.Autowired;

/**
 * Class which has several constructors, but only one annotated with
 * {@link Autowired}. The other constructor may not be called.
 * 
 * @author bjoern
 */
public class MyBeanWithConstructorAutowiredBeanAndOtherConstructor {

	private MyDelegate delegate;

	@Autowired
	public MyBeanWithConstructorAutowiredBeanAndOtherConstructor(
			MyDelegate theDelegate) {
		this.delegate = theDelegate;
	}

	public MyBeanWithConstructorAutowiredBeanAndOtherConstructor() {
		throw new RuntimeException("Please call the Autowired-Konstructor!");
	}

	public MyDelegate getDelegate() {
		return delegate;
	}

}
