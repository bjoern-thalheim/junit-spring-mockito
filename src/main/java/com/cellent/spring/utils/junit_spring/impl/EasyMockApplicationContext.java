package com.cellent.spring.utils.junit_spring.impl;

import org.easymock.EasyMock;

import com.cellent.spring.utils.junit_spring.impl.backing.AbstractTestApplicationContext;

/**
 * Implementation to provide Easymock Mocks for Classes and Interfaces.
 * 
 * @author bjoern
 */
public final class EasyMockApplicationContext extends AbstractTestApplicationContext {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cellent.spring.utils.junit_spring.impl.AbstractTestApplicationContext#
	 * createMockInstance(java.lang.Class)
	 */
	@Override
	protected <T> T createMockInstance(Class<T> clazz) {
		return EasyMock.createMock(clazz);
	}

}
