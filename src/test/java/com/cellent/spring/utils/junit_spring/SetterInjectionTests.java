package com.cellent.spring.utils.junit_spring;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.cellent.spring.utils.junit_spring.support.MyBeanWithSetterAutowiredBean;
import com.cellent.spring.utils.junit_spring.support.MyDelegate;

public class SetterInjectionTests {

	/** Class under Test. */
	private BeanInstanceProvider abstractSpringMockTest;

	/**
	 * Init Class under Test.
	 */
	@Before
	public void init() {
		abstractSpringMockTest = new SpringMockitoTest();
	}

	/**
	 * Nachdem Field-Injection geht, hier der Test für die Setter-Injection.
	 */
	@Test
	public void testSetterInjection() {
		MyBeanWithSetterAutowiredBean instance = abstractSpringMockTest
				.createBean(MyBeanWithSetterAutowiredBean.class);
		assertTrue(instance.getDelegate() instanceof MyDelegate);
	}
}
