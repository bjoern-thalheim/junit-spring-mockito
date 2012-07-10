package com.cellent.spring.utils.junit_spring.support;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

/**
 * Bean with a {@link Value}-annotated method parameter in an {@link Autowired}
 * -annotated Method.
 * 
 * @author bjoern
 */
public class MyBeanWithMethodParamInjected {

	private final String value;

	@Autowired
	public MyBeanWithMethodParamInjected(
			@Value("methodParamInjectedValue") String injectedValue) {
		this.value = injectedValue;
	}

	public String getValue() {
		return value;
	}

}
