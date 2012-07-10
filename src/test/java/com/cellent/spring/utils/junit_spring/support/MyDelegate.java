package com.cellent.spring.utils.junit_spring.support;

/**
 * Delegate which shall be injected. Since you should use interfaces only for
 * such things, and mocking interfaces is easier for mocking frameworks, this is
 * an interface.
 * 
 * It could be a class too, but I'd consider this not as clean.
 * 
 * @author bjoern
 */
public interface MyDelegate {

	/**
	 * Some Method so Mockito can check if it was called.
	 */
	void executeVoidCall();

}
