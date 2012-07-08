package com.cellent.spring.utils.junit_spring;

import org.springframework.beans.factory.annotation.Value;

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

	/**
	 * Registriert einen bestimmten Value, welcher dann ggf. beim Instanziieren
	 * berücksichtigt wird.
	 * 
	 * @param key
	 *            Schlüssel des Konfigurationsparameters.
	 * @param value
	 *            Wert des Konfigurationsparameters.
	 */
	void setValue(String key, Object value);

	/**
	 * Liefert das Objekt, welches via {@link Value}-Annotation unter dem
	 * gegebenen Schlüsselwert registriert wurde.
	 * 
	 * @param value
	 *            Der Key der {@link Value}-Annotation.
	 * @return Der Wert dieser Annotation.
	 */
	Object getValue(String value);

}
