package com.cellent.springockito;

import static org.junit.Assert.*;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kubek2k.springockito.annotations.SpringockitoContextLoader;
import org.springframework.beans.factory.annotation.Autowired;
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
public class FieldInjectionTest {

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

	/**
	 * Instantiation of a Bean withour any delegates autowired is verified to
	 * work in here.
	 */
	@Test
	public void testNoInjection() {
		assertTrue(bean instanceof MyBean);
	}


	/**
	 * Test Field Injection.
	 */
	@Test
	public void testFieldInjection() {
		assertTrue(myBeanWithFieldAutowiredBean instanceof MyBeanWithFieldAutowiredBean);
		assertEquals(delegateMock, myBeanWithFieldAutowiredBean.getDelegate());
	}

}
