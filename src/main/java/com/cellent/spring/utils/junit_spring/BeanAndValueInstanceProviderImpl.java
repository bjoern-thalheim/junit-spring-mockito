package com.cellent.spring.utils.junit_spring;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.mockito.Mockito;
import org.springframework.beans.TypeConverter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.DependencyDescriptor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericApplicationContext;

/**
 * Klasse, welche eine Art ApplicationContext für Tests ausführt, damit
 * Field-Injection gleich so geht und kein Setter für das injizierte Bean nur
 * für den Test eingeführt werden muss.
 * 
 * @author bjoern
 */
public final class BeanAndValueInstanceProviderImpl implements BeanAndValueInstanceProvider {

	/**
	 * Eine Map mit allen {@link Value}s.
	 */
	private Map<String, Object> atValueMap;

	/**
	 * Cache für Beans im Applicationcontext. Wird beim
	 * {@link MockitoTestBeanFactory#resolveDependency(DependencyDescriptor, String, Set, TypeConverter)}
	 * verwendet.
	 */
	@SuppressWarnings("rawtypes")
	private Map<Class, Object> mockInstanceMap;

	/**
	 * Um Injection (Field und Method) auf blanken Beans ausführen zu können.
	 */
	private AutowiredAnnotationBeanPostProcessor autowirePostProcessor;

	/**
	 * Um Instanziierung via Spring (mit Konstriktor-Injection) durchführen zu
	 * können.
	 */
	ApplicationContext applicationContext;

	/**
	 * Erzeuge eine Instanz, welche es ermöglicht Spring-DI auch beim Erzeugen
	 * von Testklassen zu verwenden. Hierzu muss man die Instanziierung der
	 * Klasse der Methode {@link #createBean(Class)} überlassen.
	 */
	@SuppressWarnings("rawtypes")
	public BeanAndValueInstanceProviderImpl() {
		// Initialisiere den Object Cache ({@link #mockInstanceMap},
		// Pseudo-ApplicationContext) und den {@link #autowirePostProcessor}.
		mockInstanceMap = new HashMap<Class, Object>();
		atValueMap = new HashMap<String, Object>();
		// Spring Infrastruktur
		autowirePostProcessor = new AutowiredAnnotationBeanPostProcessor();
		applicationContext = new GenericApplicationContext(
				new MockitoTestBeanAndValueFactory(this));
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
		// Führe Field Injection aus.
		autowirePostProcessor.processInjection(result);
		if (result instanceof InitializingBean) {
			try {
				((InitializingBean) result).afterPropertiesSet();
			} catch (Exception e) {
				throw new RuntimeException(
						"Class is InitializingBean, but calling afterPropertiesSet leads to an error: "
								+ e.getMessage(), e);
			}
		}
		return result;
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
	 * @see com.cellent.spring.utils.junit_spring.BeanInstanceProvider#getInstanceOf(java.lang.Class)
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
	 * @see com.cellent.spring.utils.junit_spring.BeanAndValueInstanceProvider#setValue(java.lang.String, java.lang.Object)
	 */
	public void setValue(String key, Object value) {
		atValueMap.put(key, value);
	}

	/*
	 * (non-Javadoc)
	 * @see com.cellent.spring.utils.junit_spring.BeanInstanceProvider#getValue(java.lang.String)
	 */
	public Object getValue(String value) {
		return atValueMap.get(value);
	}

	/**
	 * Suche nach einer gegebenen Klasse in der Menge bekannter Instanzen. Wird
	 * eine gefunden, word das Paar von Klasse und Instanz in
	 * {@link #mockInstanceMap} eingetragen und true zurück gegeben, sonst
	 * false.
	 * 
	 * @param clazz
	 *            Die gesuchte Klasse.
	 * @return true, wenn {@link #mockInstanceMap} eine Instanz der Klasse hat,
	 *         sonst false.
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
	 * Erzeuge eine Instanz der gewünschten Klasse mit Mockito.
	 * 
	 * @param requiredType
	 *            Die gewünschte Klasse.
	 * @return Eine Mocj-Instanz der gewünschten Klasse.
	 */
	private <T> T createMockInstance(Class<T> requiredType) {
		return Mockito.mock(requiredType);
	}

}
