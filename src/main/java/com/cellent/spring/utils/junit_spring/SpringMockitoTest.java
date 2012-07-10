package com.cellent.spring.utils.junit_spring;

import org.mockito.Mockito;

/**
 * Implementation to provide Mockito Mocks for Classes and Interfaces.
 * 
 * @author bjoern
 */
public final class SpringMockitoTest extends AbstractSpringMockTest {

	/*
	 * (non-Javadoc)
	 * @see com.cellent.spring.utils.junit_spring.AbstractSpringMockTest#createMockInstance(java.lang.Class)
	 */
	protected <T> T createMockInstance(Class<T> requiredType) {
		return Mockito.mock(requiredType);
	}

}
