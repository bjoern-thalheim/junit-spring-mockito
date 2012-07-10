package com.cellent.spring.utils.junit_spring;

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

/**
 * Bean factory, which uses {@link BeanInstanceProvider#getInstanceOf(Class)}
 * and {@link BeanInstanceProvider#getValue(String)} to to autowiring without a
 * spring context.
 * 
 * @author bjoern
 */
class SpringMockBeanFactory extends DefaultListableBeanFactory {

	/**
	 * The {@link BeanInstanceProvider} which takes care of object creation and
	 * management.
	 */
	private final BeanInstanceProvider beanInstanceProvider;

	/**
	 * Constructor of this class.
	 * 
	 * @param beanInstanceProvider
	 *            The {@link BeanInstanceProvider} which takes care of object
	 *            creation and management.
	 */
	SpringMockBeanFactory(BeanInstanceProvider abstractSpringMockTest) {
		this.beanInstanceProvider = abstractSpringMockTest;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.beans.factory.support.DefaultListableBeanFactory
	 * #getBean(java.lang.Class)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <T> T getBean(Class<T> requiredType) throws BeansException {
		if (beanInstanceProvider.isUsedByApplicationContextAware()) {
			return beanInstanceProvider.getInstanceOf(requiredType);
		}
		Constructor<?> constructor = getAutowiredOrOnlyConstructorOf(requiredType);
		Object[] constructorArguments = findOrInstantiate(
				constructor.getParameterTypes(),
				constructor.getParameterAnnotations());
		try {
			return (T) constructor.newInstance(constructorArguments);
		} catch (Exception e) {
			throw new UnsupportedOperationException(
					"Error creating a new instance of "
							+ requiredType.getCanonicalName()
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
	 * @param paramTypes
	 *            A set of method parameter classes.
	 * @param parameterAnnotations
	 *            The annotations on these method parameters.
	 * @return Instances of the desired method parameter classes.
	 */
	private Object[] findOrInstantiate(Class<?>[] paramTypes,
			Annotation[][] parameterAnnotations) {
		Object[] result = new Object[paramTypes.length];
		for (int i = 0; i < paramTypes.length; i++) {
			// It is possible a method param is annotated with @Value. If so,
			// finding this value is delegated to another method.
			Annotation[] currentAnnotations = parameterAnnotations[i];
			Object injectedValue = evaluateMethodParamAnnotations(currentAnnotations);
			if (injectedValue != null) {
				result[i] = injectedValue;
			} else {
				// The method param is not annotated with @Value. We'll look
				// into the context to find
				// an instance of this class.
				Class<?> paramType = paramTypes[i];
				Object paramInstance = this.beanInstanceProvider
						.getInstanceOf(paramType);
				result[i] = paramInstance;
			}
		}
		return result;
	}

	/**
	 * Evaluate the annotations of a method parameter. If a {@link Value}
	 * -Annotation is found, {@link BeanInstanceProvider#getValue(String)} will
	 * be used to find the correct instance for the method param.
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
		result = beanInstanceProvider.getValue(valueKey);
		if (result != null || nullIsLegal) {
			return result;
		}
		throw new IllegalArgumentException("@Value-Annotation with key "
				+ valueKey + ", but no value registered under this key.");
	}

	/**
	 * Get the constructor of the desired class.
	 * 
	 * @param requiredType
	 *            desired Class.
	 * @return The constructor of the given class.
	 */
	private <T> Constructor<?> getAutowiredOrOnlyConstructorOf(
			Class<T> requiredType) {
		Constructor<?>[] constructors = requiredType.getConstructors();
		// If there exists only one constructor, ...
		if (constructors.length == 1) {
			// ... and its parameterless, this is the default constructor ...
			if (constructors[0].getParameterTypes().length == 0) {
				// ... and my be returned.
				return constructors[0];
			}
		}
		List<Constructor<?>> autowiredConstructors = filterAutowiredConstructors(constructors);
		return autowiredConstructors.get(0);
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
	private List<Constructor<?>> filterAutowiredConstructors(
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
		return result;
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
		Class<?> desiredClass = determineDesiredClassFromFieldOrMethod(descriptor);
		return this.beanInstanceProvider.getInstanceOf(desiredClass);
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
		Class<?> desiredClass;
		if (field instanceof Field) {
			desiredClass = field.getType();
		} else {
			MethodParameter method = descriptor.getMethodParameter();
			desiredClass = method.getParameterType();
		}
		return desiredClass;
	}
}