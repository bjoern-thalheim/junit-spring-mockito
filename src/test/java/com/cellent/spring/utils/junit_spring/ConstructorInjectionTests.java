package com.cellent.spring.utils.junit_spring;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.cellent.spring.utils.junit_spring.support.MyBeanWithConstructorAutowiredBean;
import com.cellent.spring.utils.junit_spring.support.MyBeanWithConstructorAutowiredBeanAndOtherConstructor;
import com.cellent.spring.utils.junit_spring.support.MyBeanWithNonDefaultNonAutowiredConstructor;
import com.cellent.spring.utils.junit_spring.support.MyDelegate;

/**
 * Test case for all kind of usages of {@link Autowired} within constructors.
 * 
 * @author bjoern
 */
public class ConstructorInjectionTests {

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
	 * Check if Constructor-Injection works in a constructor with exactly one
	 * {@link Autowired} argument.
	 */
	@Test
	public void testConstructorInjection() {
		MyBeanWithConstructorAutowiredBean instance = abstractSpringMockTest
				.createBean(MyBeanWithConstructorAutowiredBean.class);
		assertTrue(instance.getDelegate() instanceof MyDelegate);
		MyDelegate delegate = abstractSpringMockTest
				.getInstanceOf(MyDelegate.class);
		assertTrue(delegate == instance.getDelegate());

	}

	/**
	 * If there exist several constructors, the one annotated with
	 * {@link Autowired} needs to be used.
	 */
	@Test
	public void testMultipleConstructors() {
		MyBeanWithConstructorAutowiredBeanAndOtherConstructor instance = abstractSpringMockTest
				.createBean(MyBeanWithConstructorAutowiredBeanAndOtherConstructor.class);
		MyDelegate delegate = abstractSpringMockTest
				.getInstanceOf(MyDelegate.class);
		assertTrue(delegate == instance.getDelegate());
	}

	/**
	 * If there exists a constructor which is not the default constructor and
	 * not annotated with {@link Autowired}, the class cannot be instantiated
	 * legally and an error needs to be thrown.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testOneConstructorDefinedButNotAutowired() {
		abstractSpringMockTest
				.createBean(MyBeanWithNonDefaultNonAutowiredConstructor.class);
	}
}
