/**
 *  DataManagerNeo4j.java
 *  coreserver
 *
 *  Created by ericpinet on 2016-05-29.
 *  Copyright (c) 2016 ConnectLife (Eric Pinet). All rights reserved.
 *
 */
package com.connectlife.coreserver.environment.data;

import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.factory.GraphDatabaseSettings;

import com.clapi.data.Data;
import com.connectlife.coreserver.Application;
import com.connectlife.coreserver.Consts;
import com.connectlife.coreserver.environment.UIDGenerator;
import com.connectlife.coreserver.environment.asset.SystemFactoryAsset;
import com.connectlife.coreserver.tools.errormanagement.StdOutErrLog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Data manager for environment using Neo4j (Graph Database: http://www.neo4j.com/) file.
 * 
 * @author ericpinet
 * <br> 2016-03-28
 */
public class DataManagerNeo4j implements DataManager {
	
	/**
	 * Logger use for this class.
	 */
	private Logger m_logger = LogManager.getLogger(getClass().getName());
	
	/**
	 * Environment data path contain the data representing the user, home, 
	 * zones, rooms, accessories
	 */
	private static final String ENV_DATA_PATH = "data";
	
	/**
	 * Environment file name contain the data representing the user, home, 
	 * zones, rooms, accessories
	 */
	private static final String ENV_DATA_FILENAME = "env.data";
	
	/**
	 * Graph database to store data.
	 */
	GraphDatabaseService m_graph;
	
	/**
	 * Path for the environment file.
	 */
	private String m_path;
	
	/**
	 * Flag to indicate if the module is correctly initialized.
	 */
	private boolean m_is_init;
	
	/**
	 * Default constructor.
	 */
	public DataManagerNeo4j(){
		m_path = "";
		m_is_init = false;
		m_graph = null;
	}
	
	/**
	 * Initialization the data manager.
	 * 
	 * @return True if initialization completed with success.
	 */
	public boolean init(){
		boolean ret_val = false;
		
		m_logger.info("Initialization in progress ...");
		
		m_path = Application.getApp().getBasePath() + "/" + ENV_DATA_PATH + "/";
		
		m_logger.info("Loading environment file: " + m_path + ENV_DATA_FILENAME + "...");
		
		m_graph =  	new GraphDatabaseFactory()
					.newEmbeddedDatabaseBuilder( new File(m_path + ENV_DATA_FILENAME) )
					.setConfig( GraphDatabaseSettings.pagecache_memory, "512M" )
					.setConfig( GraphDatabaseSettings.string_block_size, "60" )
					.setConfig( GraphDatabaseSettings.array_block_size, "300" )
					.newGraphDatabase();
			
		registerShutdownHook( m_graph );
		
		ret_val = true;
		
		m_logger.info("Initialization completed.");
		
		return m_is_init = ret_val;
	}

	/**
	 * Return True is the data manager is correctly initialized.
	 * 
	 * @return True if the data manager is correctly initialized.
	 */
	public boolean isInit(){
		return m_is_init;
	}

	/**
	 * UnInitialize the data manager. Return in empty state ready to initialize again.
	 */
	public void unInit(){
		m_is_init = false;
	}
	
	/**
	 * Check if environment file already exist. 
	 * 
	 * @return True if environment file exist.
	 */
	public boolean checkEnvironmentExist(){
		
		boolean ret_val = false;
		
		m_path = Application.getApp().getBasePath() + "/" + ENV_DATA_PATH + "/";
		
		File f = new File(m_path, ENV_DATA_FILENAME);
		if(f.exists()){
			ret_val = true;
		}
		
		return ret_val;
	}

	/**
	 * Register a shutdown hook for the Neo4j database.
	 * 
	 * @param graph
	 */
	private static void registerShutdownHook( final GraphDatabaseService _graph )
	{
	    // Registers a shutdown hook for the Neo4j instance so that it
	    // shuts down nicely when the VM exits (even if you "Ctrl-C" the
	    // running application).
	    Runtime.getRuntime().addShutdownHook( new Thread()
	    {
	        @Override
	        public void run()
	        {
	            _graph.shutdown();
	        }
	    } );
	}

	/**
	 * Return a JSON string representing the environment.
	 * 
	 * @return JSON string of the environment.
	 */
	public String getJsonEnvironment() {
		
		String ret_val = "";
		
		Data data;
		try {
			data = DataManagerFactory.prepareData(m_graph);
			
			Gson gson = new Gson();
			String json = gson.toJson(data);
			
			ret_val = json;
			
		} catch (Exception e) {
			m_logger.error(e.getMessage());
			StdOutErrLog.tieSystemOutAndErrToLog();
			e.printStackTrace();
		}
		
		return ret_val;
	}
	
	/**
	 * Return a JSON formatted string representing the environment.
	 * 
	 * @return JSON string of the environment.
	 */
	public String getJsonFormattedEnvironment() {
		String ret_val = "";
		
		Data data;
		try {
			data = DataManagerFactory.prepareData(m_graph);
			
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			String json = gson.toJson(data);
			
			ret_val = json;
			
		} catch (Exception e) {
			m_logger.error(e.getMessage());
			StdOutErrLog.tieSystemOutAndErrToLog();
			e.printStackTrace();
		}
		
		return ret_val;
	}

	/**
	 * Generate the base environment on new system. 
	 * Must be executed if environment data isn't init.
	 * 
	 * @throws Exception If something goes wrong.
	 */
	public void generateBaseEnvironnment() throws Exception {
		
		if (true == m_is_init) {
			throw new Exception("Error! Environment is already initialized. Uninit before generateBaseEnvironment.");
		}
		
		deleteEnvDirectory();
		
		GraphDatabaseService graph;
		
		m_logger.info("Creating environment file: " + m_path + ENV_DATA_FILENAME + "...");
		
		graph =  	new GraphDatabaseFactory()
					.newEmbeddedDatabaseBuilder( new File(m_path + ENV_DATA_FILENAME) )
					.setConfig( GraphDatabaseSettings.pagecache_memory, "512M" )
					.setConfig( GraphDatabaseSettings.string_block_size, "60" )
					.setConfig( GraphDatabaseSettings.array_block_size, "300" )
					.newGraphDatabase();
			
		
		try ( Transaction tx = graph.beginTx() ) {
			
			// PERSON1
			Node eric = graph.createNode(Consts.LABEL_PERSON);
			eric.setProperty(Consts.UID, UIDGenerator.getUID());
			eric.setProperty(Consts.PERSON_FIRSTNAME, "Eric");
			eric.setProperty(Consts.PERSON_LASTNAME, "Pinet");
			eric.setProperty(Consts.PERSON_IMAGEUID, "");
			
			// MAIL1
			Node mail11 = graph.createNode(Consts.LABEL_EMAIL);
			mail11.setProperty(Consts.UID, UIDGenerator.getUID());
			mail11.setProperty(Consts.EMAIL_EMAIL, "pineri01@gmail.com");
			mail11.setProperty(Consts.EMAIL_IMAGEUID, "");
			mail11.setProperty(Consts.EMAIL_TYPE, Consts.EMAIL_TYPE_PERSONAL);
			eric.createRelationshipTo(mail11, Consts.RelTypes.CONTAINS);
			
			// MAIL2
			Node mail12 = graph.createNode(Consts.LABEL_EMAIL);
			mail12.setProperty(Consts.UID, UIDGenerator.getUID());
			mail12.setProperty(Consts.EMAIL_EMAIL, "eric.pinet@imagemsoft.com");
			mail12.setProperty(Consts.EMAIL_IMAGEUID, "");
			mail12.setProperty(Consts.EMAIL_TYPE, Consts.EMAIL_TYPE_WORK);
			eric.createRelationshipTo(mail12, Consts.RelTypes.CONTAINS);
			
			// MAIL3
			Node mail13 = graph.createNode(Consts.LABEL_EMAIL);
			mail13.setProperty(Consts.UID, UIDGenerator.getUID());
			mail13.setProperty(Consts.EMAIL_EMAIL, "eric_pinet@hotmail.com");
			mail13.setProperty(Consts.EMAIL_IMAGEUID, "");
			mail13.setProperty(Consts.EMAIL_TYPE, Consts.EMAIL_TYPE_OTHER);
			eric.createRelationshipTo(mail13, Consts.RelTypes.CONTAINS);
			
			// PHONE1
			Node phone11 = graph.createNode(Consts.LABEL_PHONE);
			phone11.setProperty(Consts.UID, UIDGenerator.getUID());
			phone11.setProperty(Consts.PHONE_NUMBER, "418 998-2481");
			phone11.setProperty(Consts.PHONE_IMAGEUID, "");
			phone11.setProperty(Consts.PHONE_TYPE, Consts.PHONE_TYPE_CELL);
			eric.createRelationshipTo(phone11, Consts.RelTypes.CONTAINS);
			
			// PHONE2
			Node phone12 = graph.createNode(Consts.LABEL_PHONE);
			phone12.setProperty(Consts.UID, UIDGenerator.getUID());
			phone12.setProperty(Consts.PHONE_NUMBER, "418 548-1684");
			phone12.setProperty(Consts.PHONE_IMAGEUID, "");
			phone12.setProperty(Consts.PHONE_TYPE, Consts.PHONE_TYPE_OTHER);
			eric.createRelationshipTo(phone12, Consts.RelTypes.CONTAINS);
			
			// ADRESS
			Node address11 = graph.createNode(Consts.LABEL_ADDRESS);
			address11.setProperty(Consts.UID, UIDGenerator.getUID());
			address11.setProperty(Consts.ADDRESS_STREET, "2353 rue du cuir");
			address11.setProperty(Consts.ADDRESS_CITY, "Quebec");
			address11.setProperty(Consts.ADDRESS_REGION, "Quebec");
			address11.setProperty(Consts.ADDRESS_ZIPCODE, "G3E 0G3");
			address11.setProperty(Consts.ADDRESS_COUNTRY, "Canada");
			address11.setProperty(Consts.ADDRESS_IMAGEUID, "");
			address11.setProperty(Consts.ADDRESS_TYPE, Consts.ADDRESS_TYPE_HOME);
			eric.createRelationshipTo(address11, Consts.RelTypes.CONTAINS);
			
			// HOME
			Node home11 = graph.createNode(Consts.LABEL_HOME);
			home11.setProperty(Consts.UID, UIDGenerator.getUID());
			home11.setProperty(Consts.HOME_LABEL, "Home");
			home11.setProperty(Consts.HOME_IMAGEUID, "");
			
			// FIRST FLOOR
			Node zone11 = graph.createNode(Consts.LABEL_ZONE);
			zone11.setProperty(Consts.UID, UIDGenerator.getUID());
			zone11.setProperty(Consts.ZONE_LABEL, "First floor");
			zone11.setProperty(Consts.ZONE_IMAGEUID, "");
			
			home11.createRelationshipTo(zone11, Consts.RelTypes.CONTAINS);
			
			// LEVING ROOM
			Node room11 = graph.createNode(Consts.LABEL_ROOM);
			room11.setProperty(Consts.UID, UIDGenerator.getUID());
			room11.setProperty(Consts.ROOM_LABEL, "Leving room");
			room11.setProperty(Consts.ROOM_IMAGEUID, "");
			
			zone11.createRelationshipTo(room11, Consts.RelTypes.CONTAINS);
			
			// Create assets
			String assets [][] = SystemFactoryAsset.AssetItems;
			for (int i=0 ; i<assets.length ; i++) {
				Node asset_home = graph.createNode(Consts.LABEL_ASSET);
				asset_home.setProperty(Consts.UID, assets[i][SystemFactoryAsset.UID]);
				asset_home.setProperty(Consts.ASSET_LABEL, assets[i][SystemFactoryAsset.LABEL]);
				asset_home.setProperty(Consts.ASSET_TYPE, Consts.ASSET_TYPE_IMAGE);
				asset_home.setProperty(Consts.ASSET_MODE, Consts.ASSET_MODE_SYSTEM);
			}
			
		    tx.success();
		}
		
		graph.shutdown();
		
		m_logger.info("Creating default environment completed.");
	}

	/**
	 * Return the environment graph data.
	 * 
	 * @return Return the environment graph data.
	 * @throws Exception Throw exception if someting goes wrong.
	 */
	public GraphDatabaseService getGraph() throws Exception {
		
		if (true == m_is_init) {
			return m_graph;
		}
		else{
			throw new Exception ("Error! The DataManager must be correctly initialized before trying to get graph.");
		}
	}
	
	/**
	 * Delete environment directory.
	 */
	private void deleteEnvDirectory(){
		File directory = new File(m_path + ENV_DATA_FILENAME);
		if(directory.exists()){
			File files[] = directory.listFiles();
			
			for(int i=files.length-1 ; i>=0 ; i--){
				files[i].delete();
			}
			
			directory.delete();
		}
	}
}
