/**
 *  CmdAddZone.java
 *  coreserver
 *
 *  Created by ericpinet on 2016-06-30.
 *  Copyright (c) 2016 ConnectLife (Eric Pinet). All rights reserved.
 *
 */
package com.connectlife.coreserver.environment.cmd;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.xnap.commons.i18n.I18n;

import com.clapi.data.Home;
import com.clapi.data.Zone;
import com.connectlife.coreserver.Application;
import com.connectlife.coreserver.Consts;
import com.connectlife.coreserver.environment.UIDGenerator;
import com.connectlife.coreserver.environment.data.DataManagerNodeFactory;
import com.google.common.base.Preconditions;

/**
 * Command to add a new zone in the environment.
 * 
 * @author ericpinet
 * <br> 2016-03-28
 */
public class CmdAddZone extends CmdDefault {
	
	/**
	 * Logger use for this class.
	 */
	private Logger m_logger = LogManager.getLogger(getClass().getName());
	
	/**
	 * Initialization of translation system.
	 */
	private static I18n i18n = Application.i18n;
	
	/**
	 * Zone to add in the environment.
	 */
	private Zone m_zone;
	
	/**
	 * Home where to add the zone.
	 */
	private Home m_home;
	
	/**
	 * Default constructor.
	 *  
	 * @param _zone Zone to add in the environment.
	 * @param _home Home target where add zone.
	 */
	public CmdAddZone (Zone _zone, Home _home){
		m_zone = _zone;
		m_home = _home;
	}
	
	/**
	 * Execute the cmd on the environment.
	 * 
	 * @throws Exception Exception when something goes wrong.
	 * @see com.connectlife.coreserver.environment.cmd.Cmd#execute()
	 */
	@Override
	public void execute() throws Exception {
		
		m_logger.info(i18n.tr("Execution start ..."));
		
		Preconditions.checkNotNull(m_zone, i18n.tr("Error! It's not possible to add null zone in the environment."));
		Preconditions.checkArgument(m_zone.getUid().isEmpty(), i18n.tr("Error! It's not possible to add a zone with a UID."));
		Preconditions.checkNotNull(m_home, i18n.tr("Error! It's not possible to add room in a null home in the environment."));
		
		// get the graph data
		GraphDatabaseService graph = m_context.getDataManager().getGraph();
		
		// begin transaction
		try ( Transaction tx = graph.beginTx() ) {
			
			Node node_home = graph.findNode( Consts.LABEL_HOME, 
											 Consts.UID, 
											 m_home.getUid() );
			
			if (null != node_home) {
						
				// create the uid for the zone
				m_zone.setUid(UIDGenerator.getUID());
				
				// build zone node
				Node node = DataManagerNodeFactory.buildZoneNode(graph, m_zone);
				
				// create relationship
				node_home.createRelationshipTo(node, Consts.RelTypes.CONTAINS);
				
				// display info in log
				m_logger.info(m_zone.toString());
				
				// set the data change
				this.m_data_is_changed = true;
			}
			else {
				m_logger.error(i18n.tr("Home not found: ") + m_home.toString());
				throw new Exception(i18n.tr("Home not found: ") + m_home.toString());
			}

			tx.success();
		}
		
		m_logger.info(i18n.tr("Execution completed."));
	}	
}
