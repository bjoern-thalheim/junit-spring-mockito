package com.cellent.spring.utils.junit_spring.support;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

public class MyBeanWithMethodParamInjected {
	
	private final String value;
	
	@Autowired
	public MyBeanWithMethodParamInjected(@Value("methodParamInjectedValue") String injectedValue) {
		this.value = injectedValue;
	}

	public String getValue() {
		return value;
	}

}
