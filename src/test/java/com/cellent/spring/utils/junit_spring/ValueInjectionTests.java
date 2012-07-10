package com.cellent.spring.utils.junit_spring;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.cellent.spring.utils.junit_spring.support.MyBeanWithMethodParamInjected;
import com.cellent.spring.utils.junit_spring.support.MyBeanWithValueFieldAndSetterInjected;

/**
 * Tests für die {@link Value}-Annotation. Siehe hierzu auch:
 * http://static.springsource
 * .org/spring/docs/3.0.5.RELEASE/reference/expressions.html: 6.4.2
 * Annotation-based configuration.
 * 
 * @author bjoern
 */
public class ValueInjectionTests {

	/** Beliebiger Wert, auf welchen dann geprüft werden kann. */
	private static final String VALUE = "fvztgbuzh!";

	/** Class under Test. */
	private BeanInstanceProvider instanceProvider;

	/** Init Class under Test. */
	@Before
	public void init() {
		instanceProvider = new AbstractSpringMockitoTest();
	}

	/**
	 * Teste, dass die {@link Value}-Annotation an einem Feld gut funktioniert.
	 */
	@Test
	public void testFieldInjection() {
		instanceProvider.setValue("fieldInjectedValue", VALUE);
		MyBeanWithValueFieldAndSetterInjected instance = instanceProvider
				.createBean(MyBeanWithValueFieldAndSetterInjected.class);
		assertNull(instance.getSetterInjectedValue());
		assertEquals(VALUE, instance.getFieldInjectedValue());
	}

	/**
	 * Teste, dass die {@link Value}-Annotation an einer Methode gut
	 * funktioniert.
	 */
	@Test
	public void testMethodInjection() {
		instanceProvider.setValue("setterInjectedValue", VALUE);
		MyBeanWithValueFieldAndSetterInjected instance = instanceProvider
				.createBean(MyBeanWithValueFieldAndSetterInjected.class);
		assertNull(instance.getFieldInjectedValue());
		assertEquals(VALUE, instance.getSetterInjectedValue());
	}

	/**
	 * Teste, wie die Injection eines Methodenparameters, welcher mit
	 * {@link Value} annotiert ist, in einer Methode oder einem Konstruktor,
	 * welcher mit {@link Autowired} annotiert ist, funktioniert.
	 */
	@Test
	public void testMethodParamInjection() {
		instanceProvider.setValue("methodParamInjectedValue", VALUE);
		MyBeanWithMethodParamInjected instance = instanceProvider
				.createBean(MyBeanWithMethodParamInjected.class);
		assertEquals(VALUE, instance.getValue());
	}

	/**
	 * If there is a {@link Value}-Annotation on a method parameter, but no such
	 * value defined, an exception should be thrown in order to avoid confusion
	 * follow-up-exceptions.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testValueIsNeededButNotDefinedInMethodParam() {
		instanceProvider.createBean(MyBeanWithMethodParamInjected.class);
	}

	/**
	 * If there is a {@link Value}-Annotation on a field, but no such value
	 * defined, no exception shall be thrown. This is useful because in test
	 * scope it might be completely legal to leave values blank which you don't
	 * need.
	 */
	@Test
	public void testValueIsNeededButNotDefinedInFieldOrSetterInjection() {
		instanceProvider.setValue("fieldInjectedValue", null);
		instanceProvider.setValue("setterInjectedValue", null);
		// the next call should not throw an exception
		instanceProvider.createBean(MyBeanWithValueFieldAndSetterInjected.class);
	}

}
