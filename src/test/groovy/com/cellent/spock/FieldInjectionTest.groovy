package com.cellent.spock;

import spock.lang.Specification

import com.cellent.spring.utils.junit_spring.impl.MockitoApplicationContext
import com.cellent.spring.utils.junit_spring.support.MyBean
import com.cellent.spring.utils.junit_spring.support.MyBeanWithFieldAutowiredBean
import com.cellent.spring.utils.junit_spring.support.MyDelegate
import com.cellent.spring.utils.junit_spring.support.MyInitializingBean

/**
 * Re-Implementation of JUnit Test case with Spock!
 * 
 * @author bjoern
 */
class FieldInjectionTest extends Specification {

	/** Class under Test. */
	MockitoApplicationContext testApplicationContext

	def setup() {
		setup: "create an instance of the class under test"
		testApplicationContext = new MockitoApplicationContext()
	}

	def "Test that a bean which contains no @autowired Beans can be instantiated"() {
		when: "Create simple Bean with test application context"
		def myBean = testApplicationContext.createInstance(MyBean.class)
		then: "A non-null valid instance must be created"
		myBean instanceof MyBean
	}

	def "Test Injection by @Autowired on field"() {
		when: "Bean with one @Autowired field is created"
		MyBeanWithFieldAutowiredBean myBean = testApplicationContext.createInstance(MyBeanWithFieldAutowiredBean.class)
		then: "Object is non-null"
		myBean instanceof MyBeanWithFieldAutowiredBean
		and: "Field injection is performed and field is filled with a valid non-null instance"
		myBean.getDelegate() instanceof MyDelegate
	}

	def "Test that object obtained by MockitoApplicationContext is the same as injected into Bean"() {
		when: "create class which needs mock injected"
		MyBeanWithFieldAutowiredBean myBean = testApplicationContext.createInstance(MyBeanWithFieldAutowiredBean.class)
		def injectedDelegate = myBean.getDelegate()
		and: "Get Mock from Application Context"
		def mock = testApplicationContext.getInstance(MyDelegate.class)
		then: "Mock and injected Instance have to be the same object"
		mock == injectedDelegate
	}

	def "Like above, but in a different order"() {
		when: "Get Mock from Application Context"
		def mock = testApplicationContext.getInstance(MyDelegate.class)
		and: "create class which needs mock injected"
		MyBeanWithFieldAutowiredBean myBean = testApplicationContext.createInstance(MyBeanWithFieldAutowiredBean.class)
		def injectedDelegate = myBean.getDelegate()
		then: "Mock and injected Instance have to be the same object"
		mock == injectedDelegate
	}

	def "Test no Mock is created if an instance of the desired class is registered"() {
		setup: "create bean to inject and register it"
		MyDelegate registeredDelegate = Mock(MyDelegate)
		testApplicationContext.registerInstance(registeredDelegate)
		when: "create class which has this type injected"
		MyBeanWithFieldAutowiredBean myBean = testApplicationContext.createInstance(MyBeanWithFieldAutowiredBean.class)
		def injectedMock = myBean.getDelegate()
		then: "mock and injected delegate need to be the same"
		injectedMock == registeredDelegate
	}

	def "Test that afterPropertiesSet-Method is called"() {
		setup: "create own mock to check calls later"
		MyDelegate delegate = Mock(MyDelegate)
		testApplicationContext.registerInstance(delegate)
		when: "Create a class implementing Initilizing Bean"
		MyInitializingBean myInitializingBean = testApplicationContext.createInstance(MyInitializingBean.class)
		then: "Method in Delegate has to be called from afterPropertiesSet-Method"
		1 * delegate.executeVoidCall()
	}
}
