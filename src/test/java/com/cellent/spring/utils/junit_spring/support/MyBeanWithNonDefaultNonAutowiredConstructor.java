package com.cellent.spring.utils.junit_spring.support;

import org.springframework.beans.factory.annotation.Autowired;

/**
 * Bean which has neither a default-constructor nor an {@link Autowired}
 * -constructor. This bean cannot be instantiated in an application context.
 * This is implemented to set up tis specific error scenario and to prevent
 * accidential injection without a corresponding annotation.
 * 
 * @author bjoern
 */
public class MyBeanWithNonDefaultNonAutowiredConstructor {

	private final MyDelegate delegate;

	public MyBeanWithNonDefaultNonAutowiredConstructor(MyDelegate theDelegate) {
		this.delegate = theDelegate;
	}

	public MyDelegate getDelegate() {
		return delegate;
	}

}
