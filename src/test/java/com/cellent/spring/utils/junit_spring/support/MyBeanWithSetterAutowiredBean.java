package com.cellent.spring.utils.junit_spring.support;

import org.springframework.beans.factory.annotation.Autowired;

/**
 * If the annotation is on a setter and not on a field, injection needs to work
 * as well. This class implements that scenario.
 * 
 * @author bjoern
 */
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
