package com.cellent.spring.utils.junit_spring.support;

import org.springframework.beans.factory.annotation.Autowired;

/**
 * Simple scenario of a bean with an {@link Autowired} field.
 * 
 * @author bjoern
 */
public class MyBeanWithFieldAutowiredBean {

	@Autowired
	private MyDelegate delegate;

	public MyDelegate getDelegate() {
		return delegate;
	}

}
