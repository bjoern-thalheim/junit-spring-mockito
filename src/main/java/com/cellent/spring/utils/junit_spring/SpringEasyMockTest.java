package com.cellent.spring.utils.junit_spring;

import org.easymock.EasyMock;

/**
 * Implementation to provide Easymock Mocks for Classes and Interfaces.
 * 
 * @author bjoern
 */
public final class SpringEasyMockTest extends AbstractSpringMockTest {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cellent.spring.utils.junit_spring.AbstractSpringMockTest#
	 * createMockInstance(java.lang.Class)
	 */
	@Override
	protected <T> T createMockInstance(Class<T> requiredType) {
		return EasyMock.createMock(requiredType);
	}

}
