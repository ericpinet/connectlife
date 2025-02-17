/**
 *  CmdAddAccessory.java
 *  coreserver
 *
 *  Created by ericpinet on 2016-03-28.
 *  Copyright (c) 2016 ConnectLife (Eric Pinet). All rights reserved.
 *
 */
package com.connectlife.coreserver.environment.cmd;

import java.util.Iterator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.xnap.commons.i18n.I18n;

import com.clapi.data.Accessory;
import com.clapi.data.Room;
import com.connectlife.coreserver.Application;
import com.connectlife.coreserver.Consts;
import com.connectlife.coreserver.environment.data.DataManagerNodeFactory;
import com.connectlife.coreserver.environment.device.Device;
import com.google.api.client.util.Preconditions;

/**
 * Command to add a new accessory in the environment.
 * 
 * @author ericpinet
 * <br> 2016-03-28
 */
public class CmdAddAccessory extends CmdDefault {
	
	/**
	 * Logger use for this class.
	 */
	private Logger m_logger = LogManager.getLogger(getClass().getName());
	
	/**
	 * Initialization of translation system.
	 */
	private static I18n i18n = Application.i18n;
	
	/**
	 * Person to add in the environment.
	 */
	private Accessory m_accessory;
	
	/**
	 * Room where to add the accessory.
	 */
	private Room m_room;
	
	/**
	 * Default constructor.
	 *  
	 * @param _accessory Accessory to add in the environment.
	 * @param _room Room where add the accessory.
	 */
	public CmdAddAccessory (Accessory _accessory, Room _room){
		m_accessory = _accessory;
		m_room = _room;
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
		
		Preconditions.checkNotNull(m_accessory, i18n.tr("Error! It's not possible to add null accessory in the environment."));
		Preconditions.checkArgument(null == m_accessory.getUid() || m_accessory.getUid().isEmpty(), i18n.tr("Error! It's not possible to add a accessory with a UID."));

		// get the graph data
		GraphDatabaseService graph = m_context.getDataManager().getGraph();
		
		// begin transaction
		try ( Transaction tx = graph.beginTx() ) {
			
			// find the accessory by the serial number.
			Node node_acc = graph.findNode( Consts.LABEL_ACCESSORY, 
											Consts.ACCESSORY_SERIALNUMBER, 
											m_accessory.getSerialnumber() );
			
			Node node_room = graph.findNode( Consts.LABEL_ROOM, 
											 Consts.UID, 
											 m_room.getUid() );
			
			// check if accessory wasn't present in environment
			if (null == node_acc) {
				
				// check if room exist
				if (null != node_room) {
					
					// find accessory in network with device manager
					Iterator<Device> devices = m_context.getDeviceManager().getDevices().iterator();
					boolean found = false;
					
					while (devices.hasNext() && false == found) {
						Device device = devices.next();
						if (device.getAccessory().getSerialnumber().equals(m_accessory.getSerialnumber())) {
							m_accessory = device.getAccessory();
							found = true;
						}
					}
					
					if (found) {
					
						// build accessory node
						Node node = DataManagerNodeFactory.buildAccessoryNode(graph, m_accessory);
						
						// create relationship
						node_room.createRelationshipTo(node, Consts.RelTypes.CONTAINS);
						
						// force a synchronization with device
						m_context.getDeviceManager().forceSynchronizationOfAllDevices();
						
						// display info in log
						m_logger.info(m_accessory.toString());
						
						// set the data change
						this.m_data_is_changed = true;
					}
					else {
						m_logger.error(i18n.tr("Accessory isn't found in the network: ") + m_accessory.toString());
						throw new Exception(i18n.tr("Accessory isn't found in the network: ") + m_accessory.toString());
					}
					
				}
				else {
					m_logger.error(i18n.tr("Room not found: ") + m_room.toString());
					throw new Exception(i18n.tr("Room not found: ") + m_room.toString());
				}
			}
			else{
				m_logger.error(i18n.tr("Accessory was already added in a room. Remove the accessory before try again: ")+m_accessory.toString());
				throw new Exception(i18n.tr("Accessory was already added in a room. Remove the accessory before try again: ")+m_accessory.toString());
			}
			
			tx.success();
		}
		
		m_logger.info(i18n.tr("Execution completed."));
	}
	
	/**
	 * Return the accessory.
	 * 
	 * @return Accessory.
	 */
	public Accessory getAccessory(){
		return m_accessory;
	}
	
}
