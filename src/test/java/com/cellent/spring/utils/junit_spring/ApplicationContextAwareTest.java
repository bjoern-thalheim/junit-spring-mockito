package com.cellent.spring.utils.junit_spring;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.springframework.context.ApplicationContextAware;

import com.cellent.spring.utils.junit_spring.api.TestApplicationContext;
import com.cellent.spring.utils.junit_spring.impl.MockitoApplicationContext;
import com.cellent.spring.utils.junit_spring.support.MyApplicationContextHolder;
import com.cellent.spring.utils.junit_spring.support.MyBeanUsingAppConAwareInConstructor;
import com.cellent.spring.utils.junit_spring.support.MyBeanUsingAppContextAwareLazily;
import com.cellent.spring.utils.junit_spring.support.MyDelegate;

/**
 * In case instantiation of your class is not under Spring control, your class
 * needs to use another class which implements {@link ApplicationContextAware}
 * to get an application context and initialize other beans.
 * 
 * @author bjoern
 */
public class ApplicationContextAwareTest {

	/**
	 * Test scenario in which another Bean is not injected but rather
	 * initialized in the constructor via {@link ApplicationContextAware}.
	 * 
	 * This is a usage example, where the call to the
	 * {@link ApplicationContextAware}-Instance (for example
	 * {@link MyApplicationContextHolder#getMyDelegate()} is done during
	 * instantiation. Please note that the class under Test is instantiated via
	 * new() and not by {@link TestApplicationContext#createInstance(Class)}.
	 */
	@Test
	public void testApplicationContextAwareDuringNew() {
		TestApplicationContext testApplicationContext = new MockitoApplicationContext();
		testApplicationContext
				.initApplicationContextHolder(MyApplicationContextHolder.class);
		// somehow we'll end up in a class lookup of the delegate which should
		// be mocked then.
		MyBeanUsingAppConAwareInConstructor instance = new MyBeanUsingAppConAwareInConstructor();
		MyDelegate delegate = instance.getDelegate();
		assertTrue(delegate instanceof MyDelegate);
	}

	/**
	 * In case the call to {@link ApplicationContextAware} is done lazily, it is
	 * possible to use {@link TestApplicationContext#createInstance(Class)} to
	 * instantiate the class, because
	 * {@link TestApplicationContext#initApplicationContextHolder(Class)} is done
	 * afterwards.
	 * 
	 * Of course, using new as well is possible. It might even be smarter,
	 * because this represents usage in production scope better.
	 */
	@Test
	public void testLazyInit() {
		TestApplicationContext testApplicationContext = new MockitoApplicationContext();
		MyBeanUsingAppContextAwareLazily instance = testApplicationContext
				.createInstance(MyBeanUsingAppContextAwareLazily.class);
		testApplicationContext
				.initApplicationContextHolder(MyApplicationContextHolder.class);
		MyDelegate delegate = instance.getMyDelegate();
		assertTrue(delegate instanceof MyDelegate);
	}
}
