package com.cellent.springockito.spy;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.cellent.spring.utils.junit_spring.support.ToItselfDelegatingBean;

public class MockitoSpyDemonstrationTest {

	/**
	 * Class under Test.
	 */
	private ToItselfDelegatingBean toItselfDelegatingBean;

	/**
	 * Init class under Test.
	 */
	@Before
	public void init() {
		ToItselfDelegatingBean tmp = new ToItselfDelegatingBean();
		toItselfDelegatingBean = Mockito.spy(tmp);
	}

	/**
	 * Test that the delegating Method is really overwritten by a Mockito Spy.
	 */
	@Test
	public void test() {
		String expectedString;
		{ // setup
			expectedString = "Test123";
			Mockito.doReturn(expectedString).when(toItselfDelegatingBean).myDelegatingMethod();
			// The next String cannot work, because the real method is called then leading to an exception
			// Mockitos Javadoc gives a good picture in this concern.
			// Mockito.when(toItselfDelegatingBean.myDelegatingMethod()).thenReturn(expectedString);
		}
		String result;
		{ // when
			result = toItselfDelegatingBean.getResultString();
		}
		{ // then
			assertEquals(expectedString, result);
		}

	}

}
