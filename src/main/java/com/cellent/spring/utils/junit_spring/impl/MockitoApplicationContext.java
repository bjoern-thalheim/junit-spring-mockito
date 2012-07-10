package com.cellent.spring.utils.junit_spring.impl;

import org.mockito.Mockito;

import com.cellent.spring.utils.junit_spring.impl.backing.AbstractTestApplicationContext;

/**
 * Implementation to provide Mockito Mocks for Classes and Interfaces.
 * 
 * @author bjoern
 */
public final class MockitoApplicationContext extends AbstractTestApplicationContext {

	/*
	 * (non-Javadoc)
	 * @see com.cellent.spring.utils.junit_spring.impl.AbstractTestApplicationContext#createMockInstance(java.lang.Class)
	 */
	@Override
	protected <T> T createMockInstance(Class<T> clazz) {
		return Mockito.mock(clazz);
	}

}
