package com.cellent.spring.utils.junit_spring;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.cellent.spring.utils.junit_spring.api.TestApplicationContext;
import com.cellent.spring.utils.junit_spring.impl.MockitoApplicationContext;
import com.cellent.spring.utils.junit_spring.support.MyBeanWithMethodParamInjected;
import com.cellent.spring.utils.junit_spring.support.MyBeanWithValueFieldAndSetterInjected;

/**
 * Tests for injection via {@link Value}. See also: http://static.springsource
 * .org/spring/docs/3.0.5.RELEASE/reference/expressions.html: 6.4.2
 * Annotation-based configuration. Values need to be injected via bean post
 * processing.
 * 
 * @author bjoern
 */
public class ValueInjectionTest {

	/** Some value which can be injected and checked for afterwards in tests. */
	private static final String VALUE = "fvztgbuzh!";

	/** Class under Test. */
	private TestApplicationContext testApplicationContext;

	/** Init Class under Test. */
	@Before
	public void init() {
		testApplicationContext = new MockitoApplicationContext();
	}

	/**
	 * Test the {@link Value}-Annotation on a field.
	 */
	@Test
	public void testFieldInjection() {
		testApplicationContext.setValue("fieldInjectedValue", VALUE);
		MyBeanWithValueFieldAndSetterInjected instance = testApplicationContext
				.createInstance(MyBeanWithValueFieldAndSetterInjected.class);
		assertNull(instance.getSetterInjectedValue());
		assertEquals(VALUE, instance.getFieldInjectedValue());
	}

	/**
	 * Test {@link Value} injection on a (setter) method.
	 */
	@Test
	public void testMethodInjection() {
		testApplicationContext.setValue("setterInjectedValue", VALUE);
		MyBeanWithValueFieldAndSetterInjected instance = testApplicationContext
				.createInstance(MyBeanWithValueFieldAndSetterInjected.class);
		assertNull(instance.getFieldInjectedValue());
		assertEquals(VALUE, instance.getSetterInjectedValue());
	}

	/**
	 * A method parameter may be injected via {@link Value} if the method is
	 * annotated with {@link Autowired}.
	 */
	@Test
	public void testMethodParamInjection() {
		testApplicationContext.setValue("methodParamInjectedValue", VALUE);
		MyBeanWithMethodParamInjected instance = testApplicationContext
				.createInstance(MyBeanWithMethodParamInjected.class);
		assertEquals(VALUE, instance.getValue());
	}

	/**
	 * If there is a {@link Value}-Annotation on a method parameter, but no such
	 * value defined, an exception should be thrown in order to avoid confusion
	 * follow-up-exceptions.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testValueIsNeededButNotDefinedInMethodParam() {
		testApplicationContext.createInstance(MyBeanWithMethodParamInjected.class);
	}

	/**
	 * If there is a {@link Value}-Annotation on a field, but no such value
	 * defined, no exception shall be thrown. This is useful because in test
	 * scope it might be completely legal to leave values blank which you don't
	 * need.
	 */
	@Test
	public void testValueIsNeededButNotDefinedInFieldOrSetterInjection() {
		testApplicationContext.setValue("fieldInjectedValue", null);
		testApplicationContext.setValue("setterInjectedValue", null);
		// the next call should not throw an exception
		testApplicationContext
				.createInstance(MyBeanWithValueFieldAndSetterInjected.class);
	}

}
