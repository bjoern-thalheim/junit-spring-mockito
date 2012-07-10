package com.cellent.spring.utils.junit_spring;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.BeansException;
import org.springframework.beans.TypeConverter;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.DependencyDescriptor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.support.GenericApplicationContext;

/**
 * Class which contains a kind of application context, to make autowiring work
 * in tests without having to build up a real application context. It vontains a
 * map of Objects which may be injected as Objects and another Map which
 * contains instances which will be injected on {@link Value} annotations.
 * 
 * @author bjoern
 */
public abstract class AbstractSpringMockTest implements BeanInstanceProvider {

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
	 * @see AutowiredAnnotationBeanPostProcessor. This will be effectively
	 *      important to call
	 *      {@link SpringMockBeanFactory#resolveDependency(DependencyDescriptor, String, Set, TypeConverter)}
	 *      .
	 */
	private AutowiredAnnotationBeanPostProcessor autowirePostProcessor;

	/**
	 * To instantiate by Spring and do constructor injection. Will be using
	 * {@link SpringMockBeanFactory#getBean(Class)}.
	 */
	ApplicationContext applicationContext;

	/**
	 * If this class is used by applicationContextAware
	 * {@link BeanFactory#getBean(Class)} needs to instantiate Mocks but no real
	 * instances. To make this distinction possible, we need this switch.
	 */
	private boolean usedByApplicationContextAware;

	/**
	 * Create an object (you might call it context or factory as well) which
	 * allows to do spring autowiring also in test classes without any special
	 * test runner. Eventually, instantiate the class under Test by
	 * {@link #createBean(Class)}.
	 */
	@SuppressWarnings("rawtypes")
	public AbstractSpringMockTest() {
		// Init the object cache ({@link #mockInstanceMap},
		// Pseudo-ApplicationContext) and the {@link #autowirePostProcessor}.
		mockInstanceMap = new HashMap<Class, Object>();
		atValueMap = new HashMap<String, Object>();
		// Spring Infrastructure
		autowirePostProcessor = new AutowiredAnnotationBeanPostProcessor();
		applicationContext = new GenericApplicationContext(
				new SpringMockBeanFactory(this));
		autowirePostProcessor.setBeanFactory(applicationContext
				.getAutowireCapableBeanFactory());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.cellent.spring.utils.junit_spring.BeanInstanceProvider#createBean
	 * (java.lang.Class)
	 */
	public <T> T createBean(Class<T> desiredClass) {
		T result = applicationContext.getBean(desiredClass);
		// process field and setter injection
		autowirePostProcessor.processInjection(result);
		executeAfterPropertiesSetIfNecessary(result);
		return result;
	}

	/**
	 * If the class implements {@link InitializingBean}, the
	 * afterPropertiesSet-Method needs to be executed.
	 * 
	 * @param result
	 *            An instantiated bean which may inmplement
	 *            {@link InitializingBean}.
	 */
	private <T> void executeAfterPropertiesSetIfNecessary(T result) {
		if (result instanceof InitializingBean) {
			try {
				((InitializingBean) result).afterPropertiesSet();
			} catch (Exception e) {
				throw new RuntimeException(
						"Class is InitializingBean, but calling afterPropertiesSet leads to an error: "
								+ e.getMessage(), e);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.cellent.spring.utils.junit_spring.BeanInstanceProvider#registerInstance
	 * (java.lang.Object)
	 */
	public void registerInstance(Object beanInstance) {
		mockInstanceMap.put(beanInstance.getClass(), beanInstance);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.cellent.spring.utils.junit_spring.BeanInstanceProvider#getInstanceOf
	 * (java.lang.Class)
	 */
	@SuppressWarnings("unchecked")
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
	 * @see
	 * com.cellent.spring.utils.junit_spring.BeanInstanceProvider#setValue(java
	 * .lang.String, java.lang.Object)
	 */
	public void setValue(String key, Object value) {
		atValueMap.put(key, value);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.cellent.spring.utils.junit_spring.BeanInstanceProvider#getValue(java
	 * .lang.String)
	 */
	public Object getValue(String value) {
		return atValueMap.get(value);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cellent.spring.utils.junit_spring.BeanInstanceProvider#
	 * initApplicationContextHolder(java.lang.Class)
	 */
	public void initApplicationContextHolder(
			Class<? extends ApplicationContextAware> applicationContextAware) {
		try {
			applicationContextAware.newInstance().setApplicationContext(
					this.applicationContext);
			// after using this, getBean needs to behave differently, so this
			// switch was included.
			usedByApplicationContextAware = true;
		} catch (BeansException e) {
			throw new RuntimeException(
					"I should be able to instantiate the applicationContextAware ...",
					e);
		} catch (InstantiationException e) {
			throw new RuntimeException(
					"I should be able to instantiate the applicationContextAware ...",
					e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(
					"I should be able to instantiate the applicationContextAware ...",
					e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cellent.spring.utils.junit_spring.BeanInstanceProvider#
	 * isUsedByApplicationContextAware()
	 */
	public boolean isUsedByApplicationContextAware() {
		return usedByApplicationContextAware;
	}

	/**
	 * Search for a class in a set of known instances. If one is found, a pair
	 * of class/object will be cached in {@link #mockInstanceMap} and true is
	 * returned, otherwise false.
	 * 
	 * If true is returned, you can obtain your instance via
	 * {@link Map#get(Object)} on {@link #mockInstanceMap}.
	 * 
	 * @param clazz
	 *            The class you are looking for.
	 * @return true, if {@link #mockInstanceMap} holds an instance of this
	 *         class, false otherwise.
	 */
	private <T> boolean discoverInstanceOf(Class<T> clazz) {
		Collection<Object> instaces = mockInstanceMap.values();
		for (Object object : instaces) {
			if (clazz.isInstance(object)) {
				mockInstanceMap.put(clazz, object);
				return true;
			}
		}
		return false;
	}

	/**
	 * Create a mocked instance of the desired class (for example via EasyMock or Mockito).
	 * 
	 * @param requiredType
	 *            The desired class.
	 * @return A Mock of the desired class.
	 */
	protected abstract <T> T createMockInstance(Class<T> requiredType);
}
