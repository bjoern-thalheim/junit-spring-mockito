package com.cellent.spring.utils.junit_spring;

public interface BeanInstanceProvider {

	/**
	 * Finde im Kontext eine Instanz der gewünschten Klasse. Ist keine bekannt,
	 * wird ein Mock erzeugt und im Kontext registriert.
	 * 
	 * @param clazz
	 *            Die gewünschte Klasse.
	 * @return Eine Instanz der gewünschten Klasse, entweder eine vorher
	 *         registrierte Klasse oder ein neu erzeugtes Mock dieser Klasse.
	 */
	<T> T getInstanceOf(Class<T> clazz);

	/**
	 * Erzeuge eine Instanz der gegebenen Klasse (kein Mock!). Alle Felder
	 * dieser Instanz, welche Beans via Autowired injected haben, werden
	 * befüllt.
	 * 
	 * @param desiredClass
	 *            Die gewünschte Klasse. Darf kein Interface sein.
	 * 
	 * @return Eine Instanz der gewünschen Klasse, in welcher alle Felder,
	 *         welche mit Autowired annotiert sind, gefüllt sind.
	 */
	<T> T createBean(Class<T> desiredClass);

	/**
	 * Registriere das gegebene Objekt im Applicationskontext. Wann immer
	 * nachfolgend in neu erzeugten Instanzen eine INstanz der gegebenen Klasse
	 * gesucht wird, wird die hier gegebene zurück gegeben.
	 * 
	 * @param beanInstance
	 *            Eine Instanz einer bestimmten Klasse.
	 */
	void registerInstance(Object beanInstance);

}
