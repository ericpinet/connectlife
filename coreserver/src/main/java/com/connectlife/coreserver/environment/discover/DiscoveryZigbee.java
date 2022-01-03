/**
 *  DiscoveryZigbee.java
 *  coreserver
 *
 *  Created by ericpinet on 2016-04-30.
 *  Copyright (c) 2016 ConnectLife (Eric Pinet). All rights reserved.
 *
 */
package com.connectlife.coreserver.environment.discover;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.connectlife.coreserver.tools.errormanagement.StdOutErrLog;
import com.digi.xbee.api.XBeeDevice;
import com.digi.xbee.api.exceptions.XBeeException;

/**
 * Zigbee discovery service. 
 * 
 * @author ericpinet
 * <br> 2016-04-30
 */
public class DiscoveryZigbee implements DiscoveryService {
	
	/**
	 * Logger use for this class.
	 */
	private static Logger m_logger = LogManager.getLogger(DiscoveryZigbee.class);

	/**
	 * Port of the zigbee USB device
	 */
    private static final String _PORT_ = "/dev/tty.usbserial-DA01LPXS";
    
    /**
     * Baud rate of the zigbee device.
     */
    private static final int _BAUD_RATE_ = 9600;
    
    /**
     * XbeeDevice to communicate with Xbee module
     */
    XBeeDevice m_xbee;
	
	/**
	 * 
	 * @see com.connectlife.coreserver.environment.discover.DiscoveryService#start()
	 */
	@Override
	public void start() {
		
		// TODO: Remove this sample code
		// start the xbee module
		m_xbee = new XBeeDevice(_PORT_, _BAUD_RATE_);
		try{
			m_logger.info("Opening xbee module "+_PORT_+":"+_BAUD_RATE_+" ...");
			m_xbee.open();
			m_logger.info("Opened");
			
			
			m_xbee.sendBroadcastData("XBee Eric Pinet".getBytes());
			
		}
		catch (XBeeException e) {
			m_logger.error("Unable to open xbee module :"+e.getMessage());
			StdOutErrLog.tieSystemOutAndErrToLog();
			e.printStackTrace();
		}
		catch (Exception e){
			m_logger.error("Unable to open xbee module :"+e.getMessage());
			StdOutErrLog.tieSystemOutAndErrToLog();
			e.printStackTrace();
		}
		finally {
			m_xbee.close();
        }
	}

	/**
	 * 
	 * @see com.connectlife.coreserver.environment.discover.DiscoveryService#stop()
	 */
	@Override
	public void stop() {
		if(null != m_xbee)
			m_xbee.close();	
	}

	/**
	 * @param _listner
	 * @see com.connectlife.coreserver.environment.discover.DiscoveryService#addListner(com.connectlife.coreserver.environment.discover.DiscoveryListner)
	 */
	@Override
	public void addListner(DiscoveryListner _listner) {
		// TODO Auto-generated method stub
		
	}

}
