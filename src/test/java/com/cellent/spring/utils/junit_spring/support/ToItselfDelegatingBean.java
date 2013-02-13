package com.cellent.spring.utils.junit_spring.support;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Class which delegates in a public method to itself.
 * 
 * @author bjoern
 */
public class ToItselfDelegatingBean implements InitializingBean{
	
	private boolean afterPropertiesSetExecuted = false;

	@Autowired
	private MyDelegate delegate;

	public MyDelegate getDelegate() {
		return delegate;
	}

	/**
	 * Delegate Method which I want to overwrite with a Mockito Spy. It is qualified public so it can be partial mocked in
	 * Mockito. Unless you are able to use package-private, this is the only possibility for doing that.
	 * 
	 * @return A String.
	 */
	public String myDelegatingMethod() {
		throw new UnsupportedOperationException("Please override this Method with a Mockito Spy");
	}

	/**
	 * Method which delegates simply to {@link #myDelegatingMethod()}.
	 * 
	 * @return Whatever {@link #myDelegatingMethod()} returns.
	 */
	public String getResultString() {
		return myDelegatingMethod();
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		this.afterPropertiesSetExecuted = true;
	}
	
	public boolean isAfterPropertiesSetExecuted() {
		return afterPropertiesSetExecuted;
	}

}
