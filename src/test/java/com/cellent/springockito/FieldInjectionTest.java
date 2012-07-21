package com.cellent.springockito;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kubek2k.springockito.annotations.SpringockitoContextLoader;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.SingletonBeanRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import com.cellent.spring.utils.junit_spring.support.MyBean;
import com.cellent.spring.utils.junit_spring.support.MyBeanWithFieldAutowiredBean;
import com.cellent.spring.utils.junit_spring.support.MyDelegate;

/**
 * Test case whcih tries to use Springockito to instantiate a real instance of {@link #myBeanWithFieldAutowiredBean} with a
 * Mock of {@link MyDelegate} autowired into it.
 * 
 * @author bjoern
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = SpringockitoContextLoader.class, locations = "file:src/test/resources/context.xml")
// exclude Transactional Context Listener. Seen on
// http://forum.springsource.org/showthread.php?51622-ContextConfiguration-required-Transactoins
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class })
public class FieldInjectionTest implements ApplicationContextAware {

	/**
	 * Should be a real instance containing {@link #delegateMock}.
	 */
	@Autowired
	private MyBeanWithFieldAutowiredBean myBeanWithFieldAutowiredBean;

	/**
	 * Should be a real instance.
	 */
	@Autowired
	private MyBean bean;

	/**
	 * Should be a Mockito Mock.
	 */
	@Autowired
	private MyDelegate delegateMock;

	private ApplicationContext applicationContext;

	/**
	 * Instantiation of a Bean withour any delegates autowired is verified to work in here.
	 */
	@Test
	public void testNoInjection() {
		assertTrue(bean instanceof MyBean);
	}

	/**
	 * Test Field Injection.
	 */
	@Test
	public void testWithAutowiredBean() {
		assertTrue(myBeanWithFieldAutowiredBean instanceof MyBeanWithFieldAutowiredBean);
		assertEquals(delegateMock, myBeanWithFieldAutowiredBean.getDelegate());
	}

	/**
	 * The instance injected into the class under test needs to be exactly the same as injected into this class, because
	 * this is the mock to work with in the test cases.
	 */
	@Test
	public void testGetMockAfterAutowiring() {
		MyBeanWithFieldAutowiredBean bean = this.myBeanWithFieldAutowiredBean;
		MyDelegate delegate = bean.getDelegate();
		MyDelegate mock = this.delegateMock;
		// Has to be exactly this instance, therefore comparison via ==
		assertTrue(delegate == mock);
	}

	/**
	 * Same as {@link #testGetMockAfterAutowiring()}, but in reverse order. Order shall not matter at all here.
	 */
	@Test
	public void testGetMockFirst() {
		MyDelegate mock = this.delegateMock;
		assertTrue(mock instanceof MyDelegate);
	}

	/**
	 * Registering an own instance of a delegate will by done by using {@link ApplicationContextAware}. After that, we'll
	 * try to re-init the mocked delegate and re-autowire the class under test.
	 */
	@Test
	@Ignore("It seems something like this is not possible with springockito easily")
	public void testRegisterBean() {
		MyDelegate registeredDelegate = mock(MyDelegate.class);
		String delegatebeanname = MyDelegate.class.getName();
		SingletonBeanRegistry beanFactory = (SingletonBeanRegistry) applicationContext.getAutowireCapableBeanFactory();
		beanFactory.registerSingleton(delegatebeanname, registeredDelegate);
		// Has to be exactly this instance, therefore comparison via ==
		assertTrue(registeredDelegate == applicationContext.getBean(delegatebeanname));
		MyBeanWithFieldAutowiredBean owningBean = applicationContext.getBean(MyBeanWithFieldAutowiredBean.class);
		// Re-Autowire - but this does not work unfortunately.
		applicationContext.getAutowireCapableBeanFactory().autowireBean(owningBean);
		MyDelegate injectedDelegate = owningBean.getDelegate();
		assertTrue(registeredDelegate == injectedDelegate);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.context.ApplicationContextAware#setApplicationContext(org.springframework.context.ApplicationContext
	 * )
	 */
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}
}
