package com.cellent.spring.utils.junit_spring.api;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Interface for our custom bean factory to look up instances in the "application context".
 * 
 * @author bjoern
 */
public interface BeanInstanceProvider {

	/**
	 * Find an instance of the desired class in the context. If none is known so far, a Mock of this class is created, put
	 * into the context and returned.
	 * 
	 * @param clazz
	 *            The desired class.
	 * @return An instance of the desired class, either a newly created mock or the instance already known to the context.
	 */
	<T> T getInstance(Class<T> clazz);

	/**
	 * Provide the object which was registered under the given {@link Value} -Key. If none os known, null will be returned.
	 * 
	 * @param value
	 *            The key of the {@link Value}-Annotation.
	 * @return The value of this annotation or null if no such value is known.
	 */
	Object getValue(String value);

	/**
	 * If this class is used by applicationContextAware {@link BeanFactory#getBean(Class)} needs to instantiate Mocks but
	 * no real instances. To make this distinction possible, we need this switch.
	 * 
	 * @return true, if the {@link ApplicationContext} in this class is used by an {@link ApplicationContextAware}
	 *         -Instance.
	 */
	boolean isUsedByApplicationContextAware();

	/**
	 * Returns a Bean for the given bean name.
	 * 
	 * @param name
	 *            The name of the bean.
	 * @return The bean with that name in the Application Context. If none exists, a {@link NoSuchBeanDefinitionException}
	 *         will be thrown.
	 * @throws NoSuchBeanDefinitionException
	 *             If no Bean with that name was found.
	 */
	Object getInstance(String name) throws NoSuchBeanDefinitionException;

}
