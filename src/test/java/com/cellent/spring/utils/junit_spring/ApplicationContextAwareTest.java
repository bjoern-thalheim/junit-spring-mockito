package com.cellent.spring.utils.junit_spring;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.context.ApplicationContextAware;

import com.cellent.spring.utils.junit_spring.support.MyApplicationContextHolder;
import com.cellent.spring.utils.junit_spring.support.MyBeanUsingAppConAwareInConstructor;
import com.cellent.spring.utils.junit_spring.support.MyDelegate;

/**
 * In case instantiation of your class is not under Spring control, your class
 * needs to use another class which implements {@link ApplicationContextAware}
 * to get an application context and initialize other beans.
 * 
 * @author bjoern
 * 
 */
public class ApplicationContextAwareTest {

	/**
	 * Test scenario in which another Bean is not injected but rather
	 * initialized in the constructor via {@link ApplicationContextAware}.
	 */
	@Test
	public void testApplicationContextAware() {
		BeanInstanceProvider beanInstanceProvider = new AbstractSpringMockitoTest();
		beanInstanceProvider
				.initApplicationContextHolder(MyApplicationContextHolder.class);
		MyDelegate myDelegate = Mockito.mock(MyDelegate.class);
		beanInstanceProvider.registerInstance(myDelegate);
		// somehow we'll end up in a class lookup of the delegate which should
		// be mocked then.
		MyBeanUsingAppConAwareInConstructor instance = new MyBeanUsingAppConAwareInConstructor();
		MyDelegate delegate = instance.getDelegate();
		assertTrue(delegate instanceof MyDelegate);
	}

}
