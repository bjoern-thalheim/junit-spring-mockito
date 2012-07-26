/**
 * This Package is used for demonstrating Mockitos Spy (partial Mock) functionality.
 * 
 * I personally prefer a clear composition of classes by sometimes partial mocking is necessary.
 * 
 * The first lesson learned here is the distinction between:
 *  when(spy.get(0)).thenReturn("foo");
 * and 
 *  doReturn("foo").when(spy).get(0);
 *  
 * Please have a look at the javadoc of org.mockito.Mockito.spy(Object)
 * 
 * The second lesson learned is that you can do stubbing only if the stubbed method is visible and overwritable.
 **/
package com.cellent.springockito.spy;