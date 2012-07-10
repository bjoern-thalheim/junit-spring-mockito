package com.cellent.spring.utils.junit_spring.support;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Bean implementing {@link ApplicationContextAware} to provide Access to Spring
 * Beans for non-Spring-Beans.
 * 
 * @author bjoern
 */
public class MyApplicationContextHolder implements ApplicationContextAware {

	private static ApplicationContext ctx;

	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		ctx = applicationContext;
	}

	public static MyDelegate getMyDelegate() {
		return ctx.getBean(MyDelegate.class);
	}

}
