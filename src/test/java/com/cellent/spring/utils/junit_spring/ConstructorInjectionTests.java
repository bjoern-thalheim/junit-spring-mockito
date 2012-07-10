package com.cellent.spring.utils.junit_spring;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.cellent.spring.utils.junit_spring.support.MyBeanWithConstructorAutowiredBean;
import com.cellent.spring.utils.junit_spring.support.MyBeanWithConstructorAutowiredBeanAndOtherConstructor;
import com.cellent.spring.utils.junit_spring.support.MyBeanWithNonDefaultNonAutowiredConstructor;
import com.cellent.spring.utils.junit_spring.support.MyDelegate;

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
	 * Prüfe, ob Konstruktor-Injection möglich ist.
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
	 * Wenn es mehrere Konstruktoren gibt, muss der ausgeführt werden, welcher
	 * mit Autowired annotiert ist.
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
	 * Wenn es einen Konstruktor gibt, dieser aber nicht Autowired ist, aber
	 * Argumente hat, dann können wir nicht wissen, wie dieser instanziiert
	 * werden muss und es muss ein Fehler kommen.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testOneConstructorDefinedButNotAutowired() {
		abstractSpringMockTest
				.createBean(MyBeanWithNonDefaultNonAutowiredConstructor.class);
	}
}
