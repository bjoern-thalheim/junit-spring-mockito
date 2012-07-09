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
class MockitoTestBeanFactory extends DefaultListableBeanFactory {

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
	MockitoTestBeanFactory(BeanInstanceProvider abstractSpringMockTest) {
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
					injectedValue = beanInstanceProvider
							.getValue(((Value) annotation).value());
					break;
				}
			}
		}
		return injectedValue;
	}

	/**
	 * Hole den Konstruktor der gegebenen Klasse.
	 * 
	 * @param requiredType
	 *            Die gewünschte Klasse.
	 * @return Den Konstructor der gegebenen Klasse.
	 */
	private <T> Constructor<?> getAutowiredOrOnlyConstructorOf(
			Class<T> requiredType) {
		Constructor<?>[] constructors = requiredType.getConstructors();
		// Wenn es nur einen Konstruktor gibt, dann ist dieser der einzige,
		// mit welchem instanziiert werden kann.
		if (constructors.length == 1) {
			// Default Konstruktor abhandeln
			if (constructors[0].getParameterTypes().length == 0) {
				return constructors[0];
			}
		}
		List<Constructor<?>> autowiredConstructors = filterAutowiredConstructors(constructors);
		return autowiredConstructors.get(0);
	}

	/**
	 * Filtert aus den gegebenen Konstruktoren alle heraus, welche mit
	 * {@link Autowired} annotiert sind.
	 * 
	 * @param constructors
	 *            Alle Konstruktoren der Klasse.
	 * @return Ein {@link Autowired}-Konstruktor.
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
			return beanInstanceProvider.getValue(valueAnnotation.value());
		}
		// Field field = descriptor.getField();
		// if (field != null) {
		// Value valueAnnotation = field.getAnnotation(Value.class);
		// if (valueAnnotation != null) {
		// return beanInstanceProvider.getValue(valueAnnotation.value());
		// }
		// }
		Class<?> desiredClass = determineDesiredClassFromFieldOrMethod(descriptor);
		return this.beanInstanceProvider.getInstanceOf(desiredClass);
	}

	/**
	 * Liefere eine Value-Annotation, falls es eine gibt. Diese kann es nur an
	 * Feldern, nicht an Methoden geben.
	 * 
	 * @param descriptor
	 *            Der von Spring gefundene Dependency-Descriptor.
	 * @return Die Value-Annotation, welche an der Dependency dran hängt, oder
	 *         null, wenn es keine gibt.
	 */
	private Value extractValueAnnotation(DependencyDescriptor descriptor) {
		Value valueAnnotation;
		if (descriptor.getField() != null) {
			valueAnnotation = descriptor.getField().getAnnotation(Value.class);
		} else {
			valueAnnotation = descriptor.getMethodParameter()
					.getMethodAnnotation(Value.class);
		}
		return (valueAnnotation != null) ? valueAnnotation : null;
	}

	/**
	 * Nimmt einen {@link DependencyDescriptor} und extrahiert daraus die
	 * Klasse. Kann entweder ein Methodenparameter oder ein Feld sein, daher ist
	 * das ein kleines bisschen komplex.
	 * 
	 * @param descriptor
	 *            Der Spring {@link DependencyDescriptor}.
	 * @return Die Klasse, welche benötigt wird.
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