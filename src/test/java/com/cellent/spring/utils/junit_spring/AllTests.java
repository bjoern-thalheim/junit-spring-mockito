package com.cellent.spring.utils.junit_spring;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ InjectionTests.class, ValueInjectionTests.class, ApplicationContextAwareTest.class})
public class AllTests {

}
