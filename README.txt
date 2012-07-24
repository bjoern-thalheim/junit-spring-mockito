This project was implemented with the goal of easing the use of Mockito or EasyMock Mocks in Spring Beans.

Usage
-----

All Tests written down in 
	com.cellent.spring.utils.junit_spring illustrate the usage of the framework.
	
Basically, depending on your preferred Mocking Framework, you create an instance of MockitoApplicationContext or EasyMockApplicationContext
	TestApplicationContext testApplicationContext = new MockitoApplicationContext();

To create your class under Test, simply use
	testApplicationContext.createInstance(Class<MyInitializingBean>)
Autowiring, and, if possible, afterPropertiesSet will be done by the framework.

To retrieve a specific mock instance, you may use
	testApplicationContext.getInstance(Class<T>)
and you'll get out the mock filled in a field of the given type in your class under test.
This way you can retrieve any mocks on which you want to set expectations or do verifications.

To register own instances in the "application context", just use
	testApplicationContext.registerInstance(Object), or
	testApplicationContext.registerInstance(String, Object)
depending on whether you'd like to register your Bean by a specific name or not.
If you have anything you have annotated with @Value, you can use
	testApplicationContext.setValue(String, Object)
to set this value.	 
If you simply don't care about a specific dependency, or you want it to be instantiated as a simple Mock, you need not to do anything.
When your Class under Test is instantiated, all fields annotated with @Autowired are automatically filled with a mock instance.
The latter means that you need to instantiate mocks yourself very often.

If your class under Test uses a Bean implementing ApplicationContextAware to get delegates, please use
	testApplicationContext.initApplicationContextHolder(Class<? extends ApplicationContextAware>)
It's best to do that after all other setup, because after that
	testApplicationContext.createInstance(Class<T>)
will return Mocks and no real instances any more. So be careful!

Supported functionality
-----------------------

Spring comes in a variety of flavors. This project enables you to test classes which ...
- use field injection via @Autowired as a field annotation
- use a constructor annotated with @Autowired
- use @Value as a parameter annotation on a parameter of a constructor annotated with @Autowired.
- use setter injection via @Autowired as a method annotation
- implement org.springframework.beans.factory.InitializingBean with #afterPropertiesSet
- use an instance of ApplicationContextHolder which finally uses org.springframework.beans.factory.BeanFactory.getBean(String) to find a bean.
- use an instance of ApplicationContextHolder which finally uses org.springframework.beans.factory.BeanFactory.getBean(Class<MyDelegate>) to find a bean.

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

But, for performance reasons, I wanted to do Spring Injection without using a real spring application container and without doing XML.
So I used Spring mechanisms outside of a Spring container to test classes which use Spring Annotations.

Other Approaches
----------------

There exists a Spring-JUnit-Testrunner, which allows you to write down an application context in xml and which actually uses Spring to do injection of these mocks into the classes under test.
I see several problems with that: 
- First, I don't like to use XML for that purpose. 
- Second, building up a real application context to run a test is in my experience slow.
(I have written some of my testcases with springockito as well and the result is: Springockito .6sec for 5 testcases, my own implementation .35sec for 19 testcases) 
- Third, manipulating the beans in your "application context" is easier with the code written down in here.
- Fourth, as soon as you'd like to use an existing production scope application context, you'll be in big trouble if that context uses placeholders which shall be filled in your build (e.g. with maven).
But yes, it is possible to use SpringJUnit4ClassRunner, and  a framework like springockito would help you to work with that.
It is also possible I have simply not found what I was looking for. I guess then this piece of software was a nice exercise.

General Approach
----------------

You can use
	org.springframework.context.support.GenericApplicationContext.GenericApplicationContext(DefaultListableBeanFactory)
with an own implementation extending 
	org.springframework.beans.factory.support.DefaultListableBeanFactory
and finally set up an AutowiredBeanPostProcessor by using
	org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor.setBeanFactory(BeanFactory)
and you're ready to go.

Field injection: If you have just fields annotated with @Autowired, you need to use
	autowirePostProcessor.processInjection(result);
For this to work, I simply had to override
	org.springframework.beans.factory.support.DefaultListableBeanFactory.resolveDependency(DependencyDescriptor, String, Set<String>, TypeConverter)
Setter injection is done by that, too.
@Value is also handled in the resolveDependency method.

Constructor injection: In order to have constructor injection work, you need to override 
	com.cellent.spring.utils.junit_spring.impl.backing.SpringMockBeanFactory.getBean(Class<T>)
appropriately and then use
	org.springframework.beans.factory.BeanFactory.getBean(Class<T>)
to instantiate your class. All Beans used in a constructor annotated with @Autowired will be again looked up by
	org.springframework.beans.factory.support.DefaultListableBeanFactory.resolveDependency(DependencyDescriptor, String, Set<String>, TypeConverter)
or, better say, the method with which I have overridden this in
	com.cellent.spring.utils.junit_spring.impl.backing.SpringMockBeanFactory.resolveDependency(DependencyDescriptor, String, Set<String>, TypeConverter)
	
Until here, everything is clearly differentiated. In our testing scenario, resolveDependencies will return delegates of the class under test which is handled by getBean.

ApplicationContextAware: Usually 
	org.springframework.beans.factory.BeanFactory.getBean(Class<T>)
will be used to look up an instance of a class when using
	org.springframework.context.ApplicationContextAware
We can simply put our own application context into the ApplicationContextAware by overriding
	org.springframework.context.ApplicationContextAware.setApplicationContext(ApplicationContext)
Now, the distinction of using getBean for real instances and the class under test and using resolveDependencies for Mocks and delegates is not given any more.
Thats why our implementation actually knows if the application context is used in an ApplicationContextAware.