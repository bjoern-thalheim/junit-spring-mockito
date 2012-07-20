package com.cellent.spring.utils.junit_spring;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.InitializingBean;

import com.cellent.spring.utils.junit_spring.api.TestApplicationContext;
import com.cellent.spring.utils.junit_spring.impl.MockitoApplicationContext;
import com.cellent.spring.utils.junit_spring.support.MyBean;
import com.cellent.spring.utils.junit_spring.support.MyBeanWithFieldAutowiredBean;
import com.cellent.spring.utils.junit_spring.support.MyDelegate;
import com.cellent.spring.utils.junit_spring.support.MyInitializingBean;

/**
 * Base scenario. "Inject" instances into classes like is done in a Spring
 * container.
 * 
 * Basically the Autowiring post processing is tested here.
 * 
 * @author bjoern
 */
public class FieldInjectionTest {

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
	 * Instantiation of a Bean withour any delegates autowired is verified to
	 * work in here.
	 */
	@Test
	public void testNoInjection() {
		MyBean bean = abstractSpringMockTest.createInstance(MyBean.class);
		assertTrue(bean instanceof MyBean);
	}

	/**
	 * One delegate is field-autowired. Please not that a Mock of the delegate
	 * does not explicitly need t be defined but is defined as needed.
	 */
	@Test
	public void testWithAutowiredBean() {
		MyBeanWithFieldAutowiredBean bean = abstractSpringMockTest
				.createInstance(MyBeanWithFieldAutowiredBean.class);
		assertTrue(bean.getDelegate() instanceof MyDelegate);
	}

	/**
	 * The instance injected into the class under test needs to be exactly the
	 * same as in {@link TestApplicationContext#getInstance(Class)}, because
	 * this is the mock to work with in the test cases.
	 */
	@Test
	public void testGetMockAfterAutowiring() {
		MyBeanWithFieldAutowiredBean bean = abstractSpringMockTest
				.createInstance(MyBeanWithFieldAutowiredBean.class);
		MyDelegate delegate = bean.getDelegate();
		MyDelegate mock = abstractSpringMockTest
				.getInstance(MyDelegate.class);
		// Has to be exactly this instance, therfore comparison via ==
		assertTrue(delegate == mock);
	}

	/**
	 * Same as {@link #testGetMockAfterAutowiring()}, but in reverse order.
	 * Order shall not matter at all here.
	 */
	@Test
	public void testGetMockFirst() {
		MyDelegate mock = abstractSpringMockTest
				.getInstance(MyDelegate.class);
		assertTrue(mock instanceof MyDelegate);
	}

	/**
	 * Test that no mock is created if one is registered explicitly before
	 * instantiating the class under test.
	 */
	@Test
	public void testRegisterBean() {
		MyDelegate registeredBean = mock(MyDelegate.class);
		abstractSpringMockTest.registerInstance(registeredBean);
		// Has to be exactly this instance, therfore comparison via ==
		assertTrue(registeredBean == abstractSpringMockTest
				.getInstance(MyDelegate.class));
		MyBeanWithFieldAutowiredBean owningBean = abstractSpringMockTest
				.createInstance(MyBeanWithFieldAutowiredBean.class);
		assertTrue(registeredBean == owningBean.getDelegate());
	}

	/**
	 * If a bean with a delegate is instantiated and no instantiation candidate
	 * for this delegate was registered before, the instance which is injected
	 * and the instance whcih can be obtained via
	 * {@link TestApplicationContext#getInstance(Class)} need to be exactly the
	 * same.
	 */
	@Test
	public void testGetCorrectMockInstance() {
		MyBeanWithFieldAutowiredBean owningBean = abstractSpringMockTest
				.createInstance(MyBeanWithFieldAutowiredBean.class);
		MyDelegate delegate = abstractSpringMockTest
				.getInstance(MyDelegate.class);
		assertTrue(delegate == owningBean.getDelegate());
		// Try again in reverse order. Re-instantiate to do so cleanly.
		abstractSpringMockTest = new MockitoApplicationContext();
		delegate = abstractSpringMockTest.getInstance(MyDelegate.class);
		owningBean = abstractSpringMockTest
				.createInstance(MyBeanWithFieldAutowiredBean.class);
		assertTrue(delegate == owningBean.getDelegate());
	}

	/**
	 * If the bean instantiated implements {@link InitializingBean},
	 * {@link InitializingBean#afterPropertiesSet()} needs to be called on the
	 * instance after instantiation.
	 */
	@Test
	public void testAfterPropertiesSet() {
		abstractSpringMockTest.createInstance(MyInitializingBean.class);
		MyDelegate delegate = abstractSpringMockTest
				.getInstance(MyDelegate.class);
		verify(delegate).executeVoidCall();
	}
}
