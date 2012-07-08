package com.cellent.spring.utils.junit_spring.support;

public interface MyDelegate {

	/**
	 * Irgendeine Methode, deren Aufruf dann in Mockito geprüft werden soll.
	 */
	void executeVoidCall();

}
