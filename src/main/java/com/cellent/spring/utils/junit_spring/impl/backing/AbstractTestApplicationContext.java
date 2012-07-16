package com.cellent.spring.utils.junit_spring.impl.backing;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.BeansException;
import org.springframework.beans.TypeConverter;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.DependencyDescriptor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.support.GenericApplicationContext;

import com.cellent.spring.utils.junit_spring.api.TestApplicationContext;
import com.cellent.spring.utils.junit_spring.support.MyDelegate;

/**
 * Class which contains a kind of application context, to make autowiring work in tests without having to build up a real
 * application context. It contains a map of Objects which may be injected as Objects and another Map which contains
 * instances which will be injected on {@link Value} annotations.
 * 
 * @author bjoern
 */
public abstract class AbstractTestApplicationContext implements TestApplicationContext {

	/**
	 * A Map with all {@link Value}s.
	 */
	private Map<String, Object> atValueMap;

	/**
	 * Cache for beans in the context. Will be used by
	 * {@link SpringMockBeanFactory#resolveDependency(DependencyDescriptor, String, Set, TypeConverter)}
	 */
	@SuppressWarnings("rawtypes")
	private Map<Class, Object> mockInstanceMap;

	/**
	 * @see AutowiredAnnotationBeanPostProcessor. This will be effectively important to call
	 *      {@link SpringMockBeanFactory#resolveDependency(DependencyDescriptor, String, Set, TypeConverter)} .
	 */
	private AutowiredAnnotationBeanPostProcessor autowirePostProcessor;

	/**
	 * To instantiate by Spring and do constructor injection. Will be using {@link SpringMockBeanFactory#getBean(Class)}.
	 */
	ApplicationContext applicationContext;

	/**
	 * If this class is used by applicationContextAware {@link BeanFactory#getBean(Class)} needs to instantiate Mocks but
	 * no real instances. To make this distinction possible, we need this switch.
	 */
	private boolean usedByApplicationContextAware;

	/**
	 * If one registers beans by specific names, here is where they are stored.
	 */
	private Map<String, Object> beanByNameMap;

	/**
	 * Create an object (you might call it context or factory as well) which allows to do spring autowiring also in test
	 * classes without any special test runner. Eventually, instantiate the class under Test by
	 * {@link #createInstance(Class)}.
	 */
	@SuppressWarnings("rawtypes")
	public AbstractTestApplicationContext() {
		// Init the object cache ({@link #mockInstanceMap},
		// Pseudo-ApplicationContext) and the {@link #autowirePostProcessor}.
		mockInstanceMap = new HashMap<Class, Object>();
		beanByNameMap = new HashMap<String, Object>();
		atValueMap = new HashMap<String, Object>();
		// Spring Infrastructure
		autowirePostProcessor = new AutowiredAnnotationBeanPostProcessor();
		applicationContext = new GenericApplicationContext(new SpringMockBeanFactory(this));
		autowirePostProcessor.setBeanFactory(applicationContext.getAutowireCapableBeanFactory());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cellent.spring.utils.junit_spring.impl.TestApplicationContext#createBean (java.lang.Class)
	 */
	@Override
	public <T> T createInstance(Class<T> clazz) {
		T result = applicationContext.getBean(clazz);
		// process field and setter injection
		autowirePostProcessor.processInjection(result);
		executeAfterPropertiesSetIfNecessary(result);
		return result;
	}

	/**
	 * If the class implements {@link InitializingBean}, the afterPropertiesSet-Method needs to be executed.
	 * 
	 * @param result
	 *            An instantiated bean which may implement {@link InitializingBean}.
	 */
	private <T> void executeAfterPropertiesSetIfNecessary(T result) {
		if (result instanceof InitializingBean) {
			InitializingBean initializingBean = (InitializingBean) result;
			safeExecuteAfterPropertiesSet(initializingBean);
		}
	}

	/**
	 * Executes {@link InitializingBean#afterPropertiesSet()}. Throws a runtime exception if something goes wrong (this
	 * should not happen).
	 * 
	 * @param initializingBean
	 *            An {@link InitializingBean}.
	 */
	private void safeExecuteAfterPropertiesSet(InitializingBean initializingBean) {
		try {
			initializingBean.afterPropertiesSet();
		} catch (Exception e) {
			throw new RuntimeException("Class is InitializingBean, but calling afterPropertiesSet leads to an error: "
					+ e.getMessage(), e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cellent.spring.utils.junit_spring.impl.TestApplicationContext#registerInstance (java.lang.Object)
	 */
	@Override
	public void registerInstance(Object beanInstance) {
		mockInstanceMap.put(beanInstance.getClass(), beanInstance);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cellent.spring.utils.junit_spring.impl.TestApplicationContext#getInstanceOf (java.lang.Class)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <T> T getInstanceOf(Class<T> clazz) {
		if (mockInstanceMap.containsKey(clazz) || discoverInstanceOf(clazz))
			return (T) mockInstanceMap.get(clazz);
		else {
			T result = createMockInstance(clazz);
			registerInstance(result);
			return result;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cellent.spring.utils.junit_spring.impl.TestApplicationContext#setValue(java .lang.String, java.lang.Object)
	 */
	@Override
	public void setValue(String key, Object value) {
		atValueMap.put(key, value);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cellent.spring.utils.junit_spring.impl.TestApplicationContext#getValue(java .lang.String)
	 */
	@Override
	public Object getValue(String value) {
		return atValueMap.get(value);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cellent.spring.utils.junit_spring.impl.TestApplicationContext#
	 * initApplicationContextHolder(java.lang.Class)
	 */
	@Override
	public void initApplicationContextHolder(Class<? extends ApplicationContextAware> applicationContextAware) {
		try {
			applicationContextAware.newInstance().setApplicationContext(this.applicationContext);
			// after using this, getBean needs to behave differently, so this
			// switch was included.
			usedByApplicationContextAware = true;
		} catch (BeansException e) {
			throw new RuntimeException("I should be able to instantiate the applicationContextAware ...", e);
		} catch (InstantiationException e) {
			throw new RuntimeException("I should be able to instantiate the applicationContextAware ...", e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException("I should be able to instantiate the applicationContextAware ...", e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cellent.spring.utils.junit_spring.impl.TestApplicationContext# isUsedByApplicationContextAware()
	 */
	public boolean isUsedByApplicationContextAware() {
		return usedByApplicationContextAware;
	}

	/**
	 * Search for a class in a set of known instances. If one is found, a pair of class/object will be cached in
	 * {@link #mockInstanceMap} and true is returned, otherwise false.
	 * 
	 * If true is returned, you can obtain your instance via {@link Map#get(Object)} on {@link #mockInstanceMap}.
	 * 
	 * @param clazz
	 *            The class you are looking for.
	 * @return true, if {@link #mockInstanceMap} holds an instance of this class, false otherwise.
	 */
	private <T> boolean discoverInstanceOf(Class<T> clazz) {
		Collection<Object> instances = mockInstanceMap.values();
		for (Object object : instances) {
			if (clazz.isInstance(object)) {
				mockInstanceMap.put(clazz, object);
				return true;
			}
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cellent.spring.utils.junit_spring.api.TestApplicationContext#registerInstance(java.lang.String,
	 * com.cellent.spring.utils.junit_spring.support.MyDelegate)
	 */
	@Override
	public void registerInstance(String name, Object instance) {
		this.beanByNameMap.put(name, instance);
	}

	/*
	 * (non-Javadoc)
	 * @see com.cellent.spring.utils.junit_spring.api.BeanInstanceProvider#getInstanceFor(java.lang.String)
	 */
	@Override
	public Object getInstanceFor(String name) throws NoSuchBeanDefinitionException {
		Object result = this.beanByNameMap.get(name);
		if (result != null) {
			return result;
		}
		throw new NoSuchBeanDefinitionException(name);
	}

	/**
	 * Create a mocked instance of the desired class (for example via EasyMock or Mockito).
	 * 
	 * @param clazz
	 *            The desired class.
	 * @return A Mock of the desired class.
	 */
	protected abstract <T> T createMockInstance(Class<T> clazz);
}
