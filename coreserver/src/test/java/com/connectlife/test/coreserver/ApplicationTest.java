/**
 *  ApplicationTest.java
 *  coreserver
 *
 *  Created by ericpinet on 2015-10-10.
 *  Copyright (c) 2015 ConnectLife (Eric Pinet). All rights reserved.
 *
 */
package com.connectlife.test.coreserver;


// external
import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.junit.contrib.java.lang.system.ExpectedSystemExit;

// internal
import com.connectlife.coreserver.Application;
import com.connectlife.coreserver.ApplicationInject;
import com.google.inject.Guice;
import com.google.inject.Injector;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Guice.class, Application.class})
@PowerMockIgnore("javax.management.*")

/**
 * 
 * 
 * @author ericpinet
 * <br> 2015-10-10
 */
public class ApplicationTest {

	@Rule
    public final ExpectedSystemExit exit = ExpectedSystemExit.none();
	
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public void injectTest() {
		Injector injector = Guice.createInjector(new ApplicationInject());
		Application app = injector.getInstance(Application.class);
		assertNotNull(app);
	}
	
	@Test
	public void mainExceptionTest() {
		
		PowerMockito.mockStatic(Guice.class);
		
		Injector injector = Mockito.mock(Injector.class);
		Application app = Mockito.mock(Application.class);
			
		try {
			Mockito.when(Guice.createInjector(Mockito.any(ApplicationInject.class))).thenReturn(injector);
			Mockito.when(injector.getInstance(Application.class)).thenReturn(app);
			Mockito.doThrow(new Exception()).when(app).startup();
		} catch (Exception e) {
			fail();
		}
		
		exit.expectSystemExitWithStatus(0);
		
		Application.main(null);
	}
	
	@Test
	public void mainTest() {
		
		PowerMockito.mockStatic(Guice.class);
		
		Injector injector = Mockito.mock(Injector.class);
		Application app = Mockito.mock(Application.class);
			
		try {
			Mockito.when(Guice.createInjector(Mockito.any(ApplicationInject.class))).thenReturn(injector);
			Mockito.when(injector.getInstance(Application.class)).thenReturn(app);
			Mockito.doNothing().when(app).startup();
		} catch (Exception e) {
			fail();
		}
		
		exit.expectSystemExitWithStatus(0);
		
		Application.main(null);
	}
	
	@Test
	public void accessorTest() {
		Injector injector = Guice.createInjector(new ApplicationInjectTest());
		final Application app = injector.getInstance(Application.class);
		
		assertNotNull(app.getApi());
		assertNull(app.getBasePath());
		assertNotNull(app.getConfig());
		assertNotNull(app.getConsole());
		assertNotNull(app.getEnvironment());
	}
	
	@Test
	public void startupTest() {
		Injector injector = Guice.createInjector(new ApplicationInjectTest());
		final Application app = injector.getInstance(Application.class);
		
		Thread test_thread = new Thread(new Runnable() {
	         public void run()
	         {
	        	 try{
	        		 app.startup();
	        	 }catch(Exception e){
	        	 }
	         }
		});
		test_thread.start();
		
		int maxtry = 100;
		int trying = 0;
		while(app.isRunning() == false && trying < maxtry){
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {}
			trying++;
		}
		
		assertTrue(app.isRunning());
	}
	
	@Test
	public void startupFailTest() {
		Injector injector = Guice.createInjector(new ApplicationInjectTest());
		final Application app = injector.getInstance(Application.class);
		
		Thread test_thread = new Thread(new Runnable() {
	         public void run()
	         {
	        	 try{
	        		 app.startup();
	        	 }catch(Exception e){
	        	 }
	         }
		});
		test_thread.start();
		
		int maxtry = 100;
		int trying = 0;
		while(app.isRunning() == false && trying < maxtry){
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {}
			trying++;
		}
		
		assertTrue(app.isRunning());
	}
	
	@Test
	public void shutdownTest() {
		Injector injector = Guice.createInjector(new ApplicationInjectTest());
		final Application app = injector.getInstance(Application.class);
		
		Thread test_thread = new Thread(new Runnable() {
	         public void run()
	         {
	        	 try{
	        		 app.startup();
	        	 }catch(Exception e){
	        	 }
	         }
		});
		test_thread.start();
		
		int maxtry = 100;
		int trying = 0;
		while(app.isRunning() == false && trying < maxtry){
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {}
			trying++;
		}
		
		Thread test2_thread = new Thread(new Runnable() {
	         public void run()
	         {
	        	 app.shutdown();
	         }
		});
		test2_thread.start();
		
		maxtry = 100;
		trying = 0;
		while(app.isRunning() == true && trying < maxtry){
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {}
			trying++;
		}
		
		assertFalse(app.isRunning());
	}

	@Test
	public void basePathTest() {
		Injector injector = Guice.createInjector(new ApplicationInjectTest());
		final Application app = injector.getInstance(Application.class);
		
		Thread test_thread = new Thread(new Runnable() {
	         public void run()
	         {
	        	 try{
	        		 app.startup();
	        	 }catch(Exception e){
	        	 }
	         }
		});
		test_thread.start();
		
		int maxtry = 100;
		int trying = 0;
		while(app.isRunning() == false && trying < maxtry){
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {}
			trying++;
		}
		
		assertTrue(app.isRunning());
		
		assertNotNull(app.getBasePath());
	}
	
	@Test
	public void startupTwiceTest() {
		Injector injector = Guice.createInjector(new ApplicationInjectTest());
		final Application app = injector.getInstance(Application.class);
		
		Thread test_thread = new Thread(new Runnable() {
	         public void run()
	         {
	        	 try{
	        		 app.startup();
	        	 }catch(Exception e){
	        	 }
	         }
		});
		test_thread.start();
		
		int maxtry = 100;
		int trying = 0;
		while(app.isRunning() == false && trying < maxtry){
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {}
			trying++;
		}
		
		Thread test2_thread = new Thread(new Runnable() {
	         public void run()
	         {
	        	 try{
	        		 app.startup();
	        	 }catch(Exception e){
	        	 }
	         }
		});
		test2_thread.start();
		
		int maxtry2 = 100;
		int trying2 = 0;
		while(app.isRunning() == true && trying2 < maxtry2){
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {}
			trying2++;
		}
		
		assertTrue(app.isRunning());
	}
}
