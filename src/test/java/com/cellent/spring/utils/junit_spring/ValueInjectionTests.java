package com.cellent.spring.utils.junit_spring;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Value;

import com.cellent.spring.utils.junit_spring.support.MyBeanWithValueInjected;

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
	private BeanAndValueInstanceProvider instanceProvider;

	/** Init Class under Test. */
	@Before
	public void init() {
		instanceProvider = new BeanAndValueInstanceProviderImpl();
	}

	/**
	 * Teste, dass die {@link Value}-Annotation an einem Feld gut funktioniert.
	 */
	@Test
	public void testFieldInjection() {
		instanceProvider.setValue("fieldInjectedValue", VALUE);
		MyBeanWithValueInjected instance = instanceProvider
				.createBean(MyBeanWithValueInjected.class);
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
		MyBeanWithValueInjected instance = instanceProvider
				.createBean(MyBeanWithValueInjected.class);
		assertNull(instance.getFieldInjectedValue());
		assertEquals(VALUE, instance.getSetterInjectedValue());
	}

	@Test
	public void testMethodParamInjection() {
		instanceProvider.setValue("methodParamInjectedValue", VALUE);
		// Hier muss immer noch die entsprechende Klasse gebaut werden, welche
		// diese Geschichte abbildet.
		fail("Not yet implemented.");
	}

}
