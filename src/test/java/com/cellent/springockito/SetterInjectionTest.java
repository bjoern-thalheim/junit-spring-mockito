package com.cellent.springockito;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.kubek2k.springockito.annotations.SpringockitoContextLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import com.cellent.spring.utils.junit_spring.support.MyBeanWithSetterAutowiredBean;
import com.cellent.spring.utils.junit_spring.support.MyDelegate;

/**
 * Demonstrate the usage of Springockito for Setter Injection via {@link Autowired}.
 * 
 * @author bjoern
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = SpringockitoContextLoader.class, locations = "file:src/test/resources/context.xml")
// exclude Transactional Context Listener. Seen on
// http://forum.springsource.org/showthread.php?51622-ContextConfiguration-required-Transactoins
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class })
public class SetterInjectionTest {

	/**
	 * Should be a real instance containing {@link #delegateMock}.
	 */
	@Autowired
	private MyBeanWithSetterAutowiredBean myBeanWithSetterAutowiredBean;

	/**
	 * Should be a Mockito Mock.
	 */
	@Autowired
	private MyDelegate delegateMock;

	/**
	 * Test Setter injection.
	 */
	@Test
	public void testSetterInjection() {
		assertTrue(myBeanWithSetterAutowiredBean instanceof MyBeanWithSetterAutowiredBean);
		assertEquals(delegateMock, myBeanWithSetterAutowiredBean.getDelegate());
	}

}
