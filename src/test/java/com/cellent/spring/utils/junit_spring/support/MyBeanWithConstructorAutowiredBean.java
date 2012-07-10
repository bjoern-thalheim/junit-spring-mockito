package com.cellent.spring.utils.junit_spring.support;

import org.springframework.beans.factory.annotation.Autowired;

/**
 * Bean which has exactly one constructor with one {@link Autowired} parameter.
 * This parameter needs to be injected during instantiation.
 * 
 * @author bjoern
 */
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
