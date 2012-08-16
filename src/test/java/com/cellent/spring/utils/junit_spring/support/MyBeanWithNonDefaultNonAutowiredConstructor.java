package com.cellent.spring.utils.junit_spring.support;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Bean which has neither a default-constructor nor an {@link Autowired} -constructor. This bean cannot be instantiated in
 * an application context. This is implemented to set up tis specific error scenario and to prevent accidential injection
 * without a corresponding annotation.
 * 
 * @author bjoern
 */
public class MyBeanWithNonDefaultNonAutowiredConstructor implements InitializingBean {

	private final MyDelegate delegate;

	public MyBeanWithNonDefaultNonAutowiredConstructor(MyDelegate theDelegate) {
		this.delegate = theDelegate;
	}

	public MyDelegate getDelegate() {
		return delegate;
	}

	@Autowired
	private MyDelegate2 fieldAutowiredDelegate;

	public MyDelegate2 getFieldAutowiredDelegate() {
		return fieldAutowiredDelegate;
	}

	private MyDelegate3 setterAutowiredDelegate;

	public MyDelegate3 getSetterAutowiredDelegate() {
		return setterAutowiredDelegate;
	}

	@Autowired
	public void setSetterAutowiredDelegate(MyDelegate3 setterAutowiredDelegate) {
		this.setterAutowiredDelegate = setterAutowiredDelegate;
	}

	private boolean afterPropertiesSetHasBeenCalled = false;

	public boolean isAfterPropertiesSetHasBeenCalled() {
		return afterPropertiesSetHasBeenCalled;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		this.afterPropertiesSetHasBeenCalled = true;
	}

}
