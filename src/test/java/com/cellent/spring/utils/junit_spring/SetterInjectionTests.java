package com.cellent.spring.utils.junit_spring;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.cellent.spring.utils.junit_spring.api.TestApplicationContext;
import com.cellent.spring.utils.junit_spring.impl.MockitoApplicationContext;
import com.cellent.spring.utils.junit_spring.support.MyBeanWithSetterAutowiredBean;
import com.cellent.spring.utils.junit_spring.support.MyDelegate;

/**
 * {@link Autowired} is not only possible on fields but also on methods. In this
 * case, the correct instantiation candidates of the method parameters need to
 * be found or created and then used to call the method.
 * 
 * @author bjoern
 */
public class SetterInjectionTests {

	/** Class under Test. */
	private TestApplicationContext abstractSpringMockTest;

	/**
	 * Init Class under Test.
	 */
	@Before
	public void init() {
		abstractSpringMockTest = new MockitoApplicationContext();
	}

	/**
	 * Test if setter injection works.
	 */
	@Test
	public void testSetterInjection() {
		MyBeanWithSetterAutowiredBean instance = abstractSpringMockTest
				.createInstance(MyBeanWithSetterAutowiredBean.class);
		assertTrue(instance.getDelegate() instanceof MyDelegate);
	}
}
