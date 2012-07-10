package com.cellent.spring.utils.junit_spring;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.InitializingBean;

import com.cellent.spring.utils.junit_spring.support.MyBean;
import com.cellent.spring.utils.junit_spring.support.MyBeanWithConstructorAutowiredBean;
import com.cellent.spring.utils.junit_spring.support.MyBeanWithConstructorAutowiredBeanAndOtherConstructor;
import com.cellent.spring.utils.junit_spring.support.MyBeanWithFieldAutowiredBean;
import com.cellent.spring.utils.junit_spring.support.MyBeanWithNonDefaultNonAutowiredConstructor;
import com.cellent.spring.utils.junit_spring.support.MyBeanWithSetterAutowiredBean;
import com.cellent.spring.utils.junit_spring.support.MyDelegate;
import com.cellent.spring.utils.junit_spring.support.MyInitializingBean;

public class FieldInjectionTests {

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
	 * Prüfung, dass ein simples Bean ohne Instanzvariablen initialisiert werden
	 * kann. Test dient der Prüfung der Instanziierung via Spring etc.
	 */
	@Test
	public void test() {
		MyBean bean = abstractSpringMockTest.createBean(MyBean.class);
		assertTrue(bean instanceof MyBean);
	}

	@Test
	public void testWithAutowiredBean() {
		MyBeanWithFieldAutowiredBean bean = abstractSpringMockTest
				.createBean(MyBeanWithFieldAutowiredBean.class);
		assertTrue(bean.getDelegate() instanceof MyDelegate);
	}

	@Test
	public void testGetMockAfterAutowiring() {
		MyBeanWithFieldAutowiredBean bean = abstractSpringMockTest
				.createBean(MyBeanWithFieldAutowiredBean.class);
		MyDelegate delegate = bean.getDelegate();
		MyDelegate mock = abstractSpringMockTest
				.getInstanceOf(MyDelegate.class);
		// Muss exakt dieselbe Instanz sein, deshalb Prüfung mit ==
		assertTrue(delegate == mock);
	}

	@Test
	public void testGetMockFirst() {
		MyDelegate mock = abstractSpringMockTest
				.getInstanceOf(MyDelegate.class);
		assertTrue(mock instanceof MyDelegate);
	}

	/**
	 * Prüfe, dass kein Mock erzeugt wird, wenn im
	 * {@link AbstractSpringMockTest} schon eine Bean dieses Typs registiert
	 * wurde.
	 */
	@Test
	public void testRegisterBean() {
		MyDelegate registeredBean = mock(MyDelegate.class);
		abstractSpringMockTest.registerInstance(registeredBean);
		// Muss exakt dieselbe Instanz sein, deshalb Prüfung mit ==
		assertTrue(registeredBean == abstractSpringMockTest
				.getInstanceOf(MyDelegate.class));
		MyBeanWithFieldAutowiredBean owningBean = abstractSpringMockTest
				.createBean(MyBeanWithFieldAutowiredBean.class);
		assertTrue(registeredBean == owningBean.getDelegate());
	}

	/**
	 * Wenn man eine Klasse mit einem Delegate erzeugt und dieses vorher nicht
	 * explizit registriert hat, muss das Ergebnis von
	 * {@link AbstractSpringMockTest#getInstanceOf(Class)} mit der Instanz des
	 * Delegates im Erzeugten Bean übereinstimmen.
	 */
	@Test
	public void testGetCorrectMockInstance() {
		MyBeanWithFieldAutowiredBean owningBean = abstractSpringMockTest
				.createBean(MyBeanWithFieldAutowiredBean.class);
		MyDelegate delegate = abstractSpringMockTest
				.getInstanceOf(MyDelegate.class);
		assertTrue(delegate == owningBean.getDelegate());
		// Nochmal ausprobieren, was passiert, wenn zuerst das Delegate und dann
		// das Bean erzeugt werden
		abstractSpringMockTest = new SpringMockitoTest();
		delegate = abstractSpringMockTest.getInstanceOf(MyDelegate.class);
		owningBean = abstractSpringMockTest
				.createBean(MyBeanWithFieldAutowiredBean.class);
		assertTrue(delegate == owningBean.getDelegate());
	}

	/**
	 * Die Methode {@link InitializingBean#afterPropertiesSet()} soll ausgeführt
	 * werden können.
	 */
	@Test
	public void testAfterPropertiesSet() {
		abstractSpringMockTest.createBean(MyInitializingBean.class);
		MyDelegate delegate = abstractSpringMockTest
				.getInstanceOf(MyDelegate.class);
		verify(delegate).executeVoidCall();
	}
}
