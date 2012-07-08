package com.cellent.spring.utils.junit_spring.support;

import org.springframework.beans.factory.annotation.Value;

public class MyBeanWithValueInjected {
	
	@Value("fieldInjectedValue")
	String value;
	
	private String setterInjectedValue;
	
	public String getFieldInjectedValue() {
		return value;
	}

	public String getSetterInjectedValue() {
		return setterInjectedValue;
	}

	@Value("setterInjectedValue")
	public void setSetterInjectedValue(String setterInjectedValue) {
		this.setterInjectedValue = setterInjectedValue;
	}

}
