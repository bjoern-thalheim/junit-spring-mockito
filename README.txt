This project was implemented with the goal of easing the use of Mockito Mocks in Spring Beans.

Initial Idea
------------

Lets Take a bean like this
	public class MyBean {
		@Autowired
		private MyDelegate mydelegate;
		...
	}
Imagine you have a test case which tests a method in MyBean which actually calls MyDelegate. What you'd like to do is replacing the instance of MyDelegate in with  mock you define:
	MyBean myBean;
	...
	MyDelegate mockedDelegate = Mockito.mock(MyDelegate.class);
	myBean.setMyDelegate(mockedDelegate);
	// ... Mockito.when(mockedDelegate.M).thenX(...)
	// myBean.callMethodUsingMyDelegate();
	// ... Mockito.verify(mockedDelegate).M
Here you have a problem: since we use field injection, we do not have a setter for MyDelegate in MyBean.

Other Approaches
----------------

As well as I know, there exists a Spring-JUnit-Testrunner again, which allows you to write down an application context in xml and which actually uses Spring to do injection of these mocks into the classes under test.
I see two problems with that: First, I don't like to use XML for that purpose. Second, building up a real application context to run a test is in my experience slow. Third, the application context is valid for all tests and not only for one of them.
It is also possible I have simply not found what I was looking for. I guess then this piece of software was a nice exercise.

Supported functionality
-----------------------

Spring comes in a variety of flavors. This projects enables you to test classes which ...
- use field injection via @Autowired as a field annotation
- use a constructor annotated with @Autowired
- use @Value as a parameter annotation on a parameter of a constructor annotated with @Autowired.
- use setter injection via @Autowired as a method annotation
- implement org.springframework.beans.factory.InitializingBean with #afterPropertiesSet
- use an instance of ApplicationContextHolder which finally uses org.springframework.beans.factory.BeanFactory.getBean(String) to find a bean.
- use an instance of ApplicationContextHolder which finally uses org.springframework.beans.factory.BeanFactory.getBean(Class<MyDelegate>) to find a bean.

General Approach
----------------