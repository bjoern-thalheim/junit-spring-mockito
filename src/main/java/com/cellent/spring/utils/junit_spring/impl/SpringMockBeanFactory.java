package com.cellent.spring.utils.junit_spring.impl;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.BeansException;
import org.springframework.beans.TypeConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.DependencyDescriptor;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.core.MethodParameter;

import com.cellent.spring.utils.junit_spring.api.BeanInstanceProvider;
import com.cellent.spring.utils.junit_spring.api.TestApplicationContext;

/**
 * Bean factory, which uses {@link TestApplicationContext#getInstanceOf(Class)}
 * and {@link TestApplicationContext#getValue(String)} to to autowiring without
 * a spring context.
 * 
 * @author bjoern
 */
class SpringMockBeanFactory extends DefaultListableBeanFactory {

	/**
	 * The {@link TestApplicationContext} which takes care of object creation
	 * and management.
	 */
	private final BeanInstanceProvider testApplicationContext;

	/**
	 * Constructor of this class.
	 * 
	 * @param testApplicationContext
	 *            The {@link TestApplicationContext} which takes care of object
	 *            creation and management.
	 */
	SpringMockBeanFactory(TestApplicationContext abstractSpringMockTest) {
		this.testApplicationContext = abstractSpringMockTest;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.beans.factory.support.DefaultListableBeanFactory
	 * #getBean(java.lang.Class)
	 */
	@Override
	public <T> T getBean(Class<T> clazz) throws BeansException {
		// create mock or find registered instance (delegate of real class).
		if (testApplicationContext.isUsedByApplicationContextAware()) {
			return testApplicationContext.getInstanceOf(clazz);
		}
		// no delegate, real class using delegates.
		return createRealInstance(clazz);
	}

	/**
	 * Create a real instance, no mock!
	 * 
	 * @param clazz
	 *            The Class which shall be created.
	 * @return An instance of the desired class.
	 */
	private <T> T createRealInstance(Class<T> clazz) {
		Constructor<?> constructor = getAutowiredOrOnlyConstructorOf(clazz);
		Object[] constructorArguments = findOrInstantiate(
				constructor.getParameterTypes(),
				constructor.getParameterAnnotations());
		return safeExecuteNewInstance(clazz, constructor,
				constructorArguments);
	}

	/**
	 * Use a {@link Constructor#newInstance()}-Call to instantiate a given
	 * class. If something goes wrong, a {@link UnsupportedOperationException}
	 * is thrown.
	 * 
	 * @param clazz
	 *            The class which is created.
	 * @param constructor
	 *            The constructor which is used.
	 * @param constructorArguments
	 *            The Constructor arguments.
	 * @return An instance created by the constructor.
	 */
	@SuppressWarnings("unchecked")
	private <T> T safeExecuteNewInstance(Class<T> clazz,
			Constructor<?> constructor, Object[] constructorArguments) {
		try {
			return (T) constructor.newInstance(constructorArguments);
		} catch (Exception e) {
			throw new UnsupportedOperationException(
					"Error creating a new instance of "
							+ clazz.getCanonicalName()
							+ "."
							+ " Maybe this class is neither instantiatable with a default Constructor,"
							+ " nor with a constructor with autowired arguments?",
					e);
		}
	}

	/**
	 * Iterate over a list of classes and annotations and finds a corresponding
	 * instance if them (both {@link Value} of {@link Autowired} object).
	 * 
	 * @param clazzes
	 *            A set of method parameter classes.
	 * @param parameterAnnotations
	 *            The annotations on these method parameters.
	 * @return Instances of the desired method parameter classes.
	 */
	private Object[] findOrInstantiate(Class<?>[] clazzes,
			Annotation[][] parameterAnnotations) {
		Object[] result = new Object[clazzes.length];
		for (int i = 0; i < clazzes.length; i++) {
			// It is possible a method param is annotated with @Value. If so,
			// finding this value is delegated to another method.
			Annotation[] currentAnnotations = parameterAnnotations[i];
			Object injectedValue = evaluateMethodParamAnnotations(currentAnnotations);
			if (injectedValue != null) {
				result[i] = injectedValue;
			} else {
				result[i] = this.testApplicationContext
						.getInstanceOf(clazzes[i]);
			}
		}
		return result;
	}

	/**
	 * Evaluate the annotations of a method parameter. If a {@link Value}
	 * -Annotation is found, {@link TestApplicationContext#getValue(String)}
	 * will be used to find the correct instance for the method param.
	 * 
	 * @param methodParameterAnnotations
	 *            The annotations of a Method parameter.
	 * @return An instance of that value or null, if no {@link Value}-annotation
	 *         is present.
	 */
	private Object evaluateMethodParamAnnotations(
			Annotation[] methodParameterAnnotations) {
		Object injectedValue = null;
		if (methodParameterAnnotations.length > 0) {
			for (Annotation annotation : methodParameterAnnotations) {
				if (annotation instanceof Value) {
					injectedValue = lookUpValue(((Value) annotation).value(),
							false);
					break;
				}
			}
		}
		return injectedValue;
	}

	/**
	 * Look up a value in the application context.
	 * 
	 * @param valueKey
	 *            The key of the value in the annotation.
	 * @param nullIsLegal
	 *            Whether a missing instance shall lead to an exception or not.
	 * @return The value registered in the application context. If none is
	 *         known, an {@link IllegalArgumentException} is thrown.
	 */
	private Object lookUpValue(String valueKey, boolean nullIsLegal) {
		Object result;
		result = testApplicationContext.getValue(valueKey);
		if (result != null || nullIsLegal) {
			return result;
		}
		throw new IllegalArgumentException("@Value-Annotation with key "
				+ valueKey + ", but no value registered under this key.");
	}

	/**
	 * Get the constructor of the desired class.
	 * 
	 * @param clazz
	 *            desired Class.
	 * @return The constructor of the given class.
	 */
	private <T> Constructor<?> getAutowiredOrOnlyConstructorOf(
			Class<T> clazz) {
		Constructor<?>[] constructors = clazz.getConstructors();
		// If there exists only one constructor, ...
		if (constructors.length == 1) {
			// ... and its parameterless, this is the default constructor ...
			if (constructors[0].getParameterTypes().length == 0) {
				// ... and should be returned.
				return constructors[0];
			}
		}
		// otherwise find autowired constructor and return this one.
		return filterAutowiredConstructors(constructors);
	}

	/**
	 * From a List of contructors, filter these which are annotated with
	 * {@link Autowired}. If there exist several, this is ambigious and will
	 * result in an Exception.
	 * 
	 * @param constructors
	 *            All Constructors of the Class.
	 * @return The {@link Autowired}-Constructor of this class.
	 */
	private Constructor<?> filterAutowiredConstructors(
			Constructor<?>[] constructors) {
		List<Constructor<?>> result = new ArrayList<Constructor<?>>(1);
		for (Constructor<?> constructor : constructors) {
			if (constructor.getAnnotation(Autowired.class) != null) {
				result.add(constructor);
			}
		}
		if (result.size() != 1) {
			throw new IllegalArgumentException(
					"Found not exactly one Autowired-Constructor, but "
							+ result.size() + " Autowired-Constructors.");
		}
		return result.get(0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.beans.factory.support.DefaultListableBeanFactory
	 * #resolveDependency
	 * (org.springframework.beans.factory.config.DependencyDescriptor,
	 * java.lang.String, java.util.Set, org.springframework.beans.TypeConverter)
	 */
	@Override
	public Object resolveDependency(DependencyDescriptor descriptor,
			String beanName, Set<String> autowiredBeanNames,
			TypeConverter typeConverter) throws BeansException {
		Value valueAnnotation = null;
		if ((valueAnnotation = extractValueAnnotation(descriptor)) != null) {
			return lookUpValue(valueAnnotation.value(), true);
		}
		Class<?> clazz = determineDesiredClassFromFieldOrMethod(descriptor);
		return this.testApplicationContext.getInstanceOf(clazz);
	}

	/**
	 * Give the {@link Value}-Annotation if one is present.
	 * 
	 * @param descriptor
	 *            The Spring {@link DependencyDescriptor}.
	 * @return The {@link Value}-Annotation if one is present, otherwise null.
	 */
	private Value extractValueAnnotation(DependencyDescriptor descriptor) {
		Value valueAnnotation;
		if (descriptor.getField() != null) {
			valueAnnotation = descriptor.getField().getAnnotation(Value.class);
		} else {
			valueAnnotation = descriptor.getMethodParameter()
					.getMethodAnnotation(Value.class);
		}
		return valueAnnotation;
	}

	/**
	 * Extracts the desired Class from the {@link DependencyDescriptor} given.
	 * 
	 * @param descriptor
	 *            The Spring {@link DependencyDescriptor}.
	 * @return The Class which is needed.
	 */
	private Class<?> determineDesiredClassFromFieldOrMethod(
			DependencyDescriptor descriptor) {
		Field field = descriptor.getField();
		Class<?> clazz;
		if (field instanceof Field) {
			clazz = field.getType();
		} else {
			MethodParameter method = descriptor.getMethodParameter();
			clazz = method.getParameterType();
		}
		return clazz;
	}
}