/**
 *  CmdUpdateAccessoryTest.java
 *  coreserver
 *
 *  Created by ericpinet on 2016-06-16.
 *  Copyright (c) 2016 ConnectLife (Eric Pinet). All rights reserved.
 *
 */
package com.connectlife.test.coreserver.environment.cmd;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.clapi.data.Accessory;
import com.connectlife.coreserver.Consts;
import com.connectlife.coreserver.environment.EnvironmentContext;
import com.connectlife.coreserver.environment.cmd.CmdFactory;
import com.connectlife.coreserver.environment.cmd.CmdUpdateAccessory;
import com.connectlife.coreserver.environment.data.DataManager;
import com.connectlife.coreserver.environment.data.DataManagerNodeFactory;

@RunWith(PowerMockRunner.class)
@PrepareForTest(DataManagerNodeFactory.class)
@PowerMockIgnore("javax.management.*")

/**
 * 
 * 
 * @author ericpinet
 * <br> 2016-06-16
 */
public class CmdUpdateAccessoryTest {

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
	public void testNullAccessory() {
		
		CmdUpdateAccessory cmd = CmdFactory.getCmdUpdateAccesssory(null);

		try {
			cmd.execute();
			fail();
		} catch (Exception e) {
			assertNotNull(e);
		}
	}
	
	@Test
	public void testGetAccessory() {
		
		Accessory accessory = new Accessory(null, null, null, null, "12345", null, null, null, null);
		CmdUpdateAccessory cmd = CmdFactory.getCmdUpdateAccesssory(accessory);

		assertTrue(accessory == cmd.getAccessory());
	}
	
	@Test
	public void testDontFindAccessory() {
		
		EnvironmentContext context = Mockito.mock(EnvironmentContext.class);
		DataManager datamanager = Mockito.mock(DataManager.class);
		GraphDatabaseService graph = Mockito.mock(GraphDatabaseService.class);
		Transaction tx = Mockito.mock(Transaction.class);
		
		Accessory accessory = new Accessory(null, null, null, null, "12345", null, null, null, null);
		
		try {
			Mockito.when(context.getDataManager()).thenReturn(datamanager);
			Mockito.when(graph.findNode(Consts.LABEL_ACCESSORY, Consts.ACCESSORY_SERIALNUMBER, accessory.getSerialnumber())).thenReturn(null);
			Mockito.when(graph.beginTx()).thenReturn(tx);
			Mockito.when(datamanager.getGraph()).thenReturn(graph);
			Mockito.doNothing().when(tx).success();
			
		} catch (Exception e1) {
			fail();
		}
		
		CmdUpdateAccessory cmd = CmdFactory.getCmdUpdateAccesssory(accessory);
		cmd.setContext(context);
		
		try {
			cmd.execute();
			fail();
		} catch (Exception e) {
			assertNotNull(e);
		}
	}
	
	@Test
	public void testComplete() {
		
		EnvironmentContext context = Mockito.mock(EnvironmentContext.class);
		DataManager datamanager = Mockito.mock(DataManager.class);
		GraphDatabaseService graph = Mockito.mock(GraphDatabaseService.class);
		PowerMockito.mockStatic(DataManagerNodeFactory.class);
		Transaction tx = Mockito.mock(Transaction.class);
		Node node = Mockito.mock(Node.class);
		
		Accessory accessory = new Accessory(null, null, null, null, "12345", null, null, null, null);
		
		try {
			Mockito.when(context.getDataManager()).thenReturn(datamanager);
			Mockito.when(graph.findNode(Consts.LABEL_ACCESSORY, Consts.ACCESSORY_SERIALNUMBER, accessory.getSerialnumber())).thenReturn(node);
			Mockito.when(graph.beginTx()).thenReturn(tx);
			Mockito.when(datamanager.getGraph()).thenReturn(graph);
			Mockito.when(node.getProperty(Consts.ACCESSORY_ISREGISTER)).thenReturn("true");
			Mockito.doNothing().when(tx).success();
			
			
		} catch (Exception e1) {
			fail();
		}
		
		CmdUpdateAccessory cmd = CmdFactory.getCmdUpdateAccesssory(accessory);
		cmd.setContext(context);
		
		try {
			cmd.execute();
			
		} catch (Exception e) {
			fail();
		}
	}
	
	

}
