package com.cellent.spring.utils.junit_spring;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import com.cellent.spring.utils.junit_spring.api.TestApplicationContext;
import com.cellent.spring.utils.junit_spring.impl.MockitoApplicationContext;
import com.cellent.spring.utils.junit_spring.support.MyBeanWithConstructorAutowiredBean;
import com.cellent.spring.utils.junit_spring.support.MyBeanWithConstructorAutowiredBeanAndOtherConstructor;
import com.cellent.spring.utils.junit_spring.support.MyBeanWithNonDefaultNonAutowiredConstructor;
import com.cellent.spring.utils.junit_spring.support.MyDelegate;
import com.cellent.spring.utils.junit_spring.support.MyDelegate2;
import com.cellent.spring.utils.junit_spring.support.MyDelegate3;

/**
 * Test case for all kind of usages of {@link Autowired} within constructors.
 * 
 * @author bjoern
 */
public class ConstructorInjectionTest {

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
	 * Check if Constructor-Injection works in a constructor with exactly one {@link Autowired} argument.
	 */
	@Test
	public void testConstructorInjection() {
		MyBeanWithConstructorAutowiredBean instance = abstractSpringMockTest
				.createInstance(MyBeanWithConstructorAutowiredBean.class);
		assertTrue(instance.getDelegate() instanceof MyDelegate);
		MyDelegate delegate = abstractSpringMockTest.getInstance(MyDelegate.class);
		assertTrue(delegate == instance.getDelegate());

	}

	/**
	 * If there exist several constructors, the one annotated with {@link Autowired} needs to be used.
	 */
	@Test
	public void testMultipleConstructors() {
		MyBeanWithConstructorAutowiredBeanAndOtherConstructor instance = abstractSpringMockTest
				.createInstance(MyBeanWithConstructorAutowiredBeanAndOtherConstructor.class);
		MyDelegate delegate = abstractSpringMockTest.getInstance(MyDelegate.class);
		assertTrue(delegate == instance.getDelegate());
	}

	/**
	 * If there exists a constructor which is not the default constructor and not annotated with {@link Autowired}, the
	 * class cannot be instantiated legally and an error needs to be thrown.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testOneConstructorDefinedButNotAutowired() {
		abstractSpringMockTest.createInstance(MyBeanWithNonDefaultNonAutowiredConstructor.class);
	}

	/**
	 * For certain scenarios (for Example, Captain Casa UI Beans), no default Constructor and no Autowired Constructor is
	 * available. However, in Tests, it is still possible to simple instantiate these classes. After that, the post
	 * processing (field and setter autowiring, calling afterPropertiesSet) can by done on that instance.
	 */
	@Test
	public void testPostProcessInjection() {
		MyBeanWithNonDefaultNonAutowiredConstructor bean = new MyBeanWithNonDefaultNonAutowiredConstructor(null);
		abstractSpringMockTest.postProcessBean(bean);
		assertEquals(abstractSpringMockTest.getInstance(MyDelegate2.class), bean.getFieldAutowiredDelegate());
		assertEquals(abstractSpringMockTest.getInstance(MyDelegate3.class), bean.getSetterAutowiredDelegate());
	}

	/**
	 * If you create an {@link InitializingBean} by calling its Constructor directly and then postprocess it, the
	 * Postprocessing also calls {@link InitializingBean#afterPropertiesSet()} on that instance.
	 */
	@Test
	public void testPostProcessAfterPropertiesSet() {
		MyBeanWithNonDefaultNonAutowiredConstructor bean = new MyBeanWithNonDefaultNonAutowiredConstructor(null);
		abstractSpringMockTest.postProcessBean(bean);
		assertTrue(bean.isAfterPropertiesSetHasBeenCalled());
	}
}
