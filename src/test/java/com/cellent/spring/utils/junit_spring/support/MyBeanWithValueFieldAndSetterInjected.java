package com.cellent.spring.utils.junit_spring.support;

import org.springframework.beans.factory.annotation.Value;

/**
 * Bean which has a {@link Value} injected on a field an another on a setter.
 * 
 * @author bjoern
 */
public class MyBeanWithValueFieldAndSetterInjected {

	@Value("fieldInjectedValue")
	private String fieldInjectedValue;

	public String getFieldInjectedValue() {
		return fieldInjectedValue;
	}

	private String setterInjectedValue;

	@Value("setterInjectedValue")
	public void setSetterInjectedValue(String setterInjectedValue) {
		this.setterInjectedValue = setterInjectedValue;
	}

	public String getSetterInjectedValue() {
		return setterInjectedValue;
	}

}
