/**
 *  DeviceMngr.java
 *  coreserver
 *
 *  Created by ericpinet on 2016-01-23.
 *  Copyright (c) 2016 ConnectLife (Eric Pinet). All rights reserved.
 *
 */
package com.connectlife.coreserver.environment.device;

import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;
import java.util.stream.Collectors;

import javax.jmdns.ServiceEvent;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.xnap.commons.i18n.I18n;

import com.clapi.data.Accessory.AccessoryProtocolType;
import com.connectlife.coreserver.Application;
import com.connectlife.coreserver.environment.discover.DiscoveryListner;
import com.connectlife.coreserver.environment.discover.DiscoveryService;
import com.connectlife.coreserver.tools.errormanagement.StdOutErrLog;
import com.google.inject.Inject;

/**
 * The device manager discover, control and monitor all services in the network.
 * 
 * @author ericpinet
 * <br> 2016-01-23
 */
public class DeviceMngr extends TimerTask implements DeviceManager, DiscoveryListner {
	
	/**
	 * Logger use for this class.
	 */
	private Logger m_logger = LogManager.getLogger(getClass().getName());
	
	/**
	 * Initialization of translation system.
	 */
	private static I18n i18n = Application.i18n;
	
	/**
	 * Flag to indicate if the module is correctly initialized.
	 */
	private boolean m_isInit;
	
	/**
	 * Discovery manager of the accessories in the environment
	 */
	private DiscoveryService m_discovery_manager;
	
	/**
	 * List of all managed services.
	 */
	private List<Device> m_devices;
	
	/**
	 * List of device that we have to unregister with the application environment.
	 */
	private List<Device> m_devices_to_unregister;
	
	/**
	 * Delay before synchronization with the application environment.
	 */
	private final int _DELAY_INTERVAL_ = 0;
	
	/**
	 * Interval between the synchronization with the application environment.
	 */
	private final int _SYNCHRONIZATION_INTERVAL_ = 1000;
	
	/**
	 * Timer for the synchronization task;
	 */
	private Timer m_timer;
	
	/**
	 * Default constructor.
	 * 
	 * @param _service DiscoveryService at use in this Environment. 
	 */
	@Inject
	public DeviceMngr(DiscoveryService _service){
		m_discovery_manager = _service;
		m_devices = new Vector<Device>();
		m_devices_to_unregister = new Vector<Device>();
		m_timer = new Timer();
	}

	/**
	 * Initialize the service manager.
	 * 
	 * @return True if initialization completed with success.
	 * @see com.connectlife.coreserver.environment.device.DeviceManager#init()
	 */
	@Override
	public boolean init() {
		
		boolean ret_val = false;
		
		m_logger.info(i18n.tr("Initialization in progress ..."));
		
		if(false == m_isInit){

			// init the service discovery
			if(null != m_discovery_manager){
				m_discovery_manager.addListner(this);
				m_discovery_manager.start();
				
				// Start the timer. 
				m_timer.schedule(this, _DELAY_INTERVAL_, _SYNCHRONIZATION_INTERVAL_); 
				
				ret_val = m_isInit = true;
			}
			else{
				m_logger.warn(i18n.tr("No discovery manager set in the environment."));
			}
			
			m_logger.info(i18n.tr("Initialization completed."));
		}
		else{
			m_logger.warn(i18n.tr("Initialization already completed."));
		}
		
		return ret_val;
	}

	/**
	 * Return true if the service manager is initialized.
	 * 
	 * @return True if initialization completed with success.
	 * @see com.connectlife.coreserver.environment.device.DeviceManager#isInit()
	 */
	@Override
	public boolean isInit() {
		return m_isInit;
	}

	/**
	 * Uninitialized the service manager.
	 * 
	 * @see com.connectlife.coreserver.environment.device.DeviceManager#unInit()
	 */
	@Override
	public void unInit() {
		
		m_logger.info(i18n.tr("UnInitialization in progress ..."));
		
		if(true == m_isInit){
		
			// Cancel the timer
			m_timer.cancel();
			
			// Stop the service discovery 
			if(null != m_discovery_manager){
				m_discovery_manager.stop();
				m_discovery_manager = null;
			}
			
			// remove all services registered in the manager
			m_devices.clear();
			
			m_isInit = false;
			
			m_logger.info(i18n.tr("UnInitialization completed."));
		}
		else{
			m_logger.warn(i18n.tr("Already unitialized."));
		}
	}
	
	/**
	 * Force all devices to synchronization with the environment.
	 * 
	 * @see com.connectlife.coreserver.environment.device.DeviceManager#forceSynchronizationOfAllDevices()
	 */
	@Override
	public void forceSynchronizationOfAllDevices() {
		// to force the synchronization of all devices, all devices set at unsynchronized.
		Iterator<Device> it = m_devices.iterator();
		while(it.hasNext()){
			Device device = it.next();
			device.unsynchronize();
		}
		
	}

	/**
	 * Callback when a service is discovered.
	 * 
	 * @param _service Service event for the service.
	 * @see com.connectlife.coreserver.environment.discover.DiscoveryListner#serviceDiscover(javax.jmdns.ServiceEvent)
	 */
	@Override
	public void serviceDiscover(ServiceEvent _service) {

		try {
			Device service = DeviceFactory.buildService(_service);
			if(null != service){
				m_devices.add(service);
			}
			
		} catch (Exception e) {
			m_logger.error(e.getMessage());
			StdOutErrLog.tieSystemOutAndErrToLog();
			e.printStackTrace();
		}
	}

	/**
	 * Callback when a service is removed.
	 * 
	 * @param _service Service event for the service.
	 * @see com.connectlife.coreserver.environment.discover.DiscoveryListner#serviceRemove(javax.jmdns.ServiceEvent)
	 */
	@Override
	public void serviceRemove(ServiceEvent _service) {
		m_logger.info("Service removed : " + _service.getName());
		
		// Remove the service
		Iterator<Device> it = m_devices.iterator();
		boolean notfound = true;
		while(notfound && it.hasNext()){
			Device device = it.next();
			
			if( device.getServiceInfo().equals(_service.getInfo()) ){
				m_devices.remove(device);
				m_devices_to_unregister.remove(device);
				m_devices_to_unregister.add(device);
				notfound = false;
			}
			
		}
	}

	/**
	 * Synchronization of all device with the application environment.
	 * If device is already synchronized, do noting.
	 * 
	 * @see java.util.TimerTask#run()
	 */
	@Override
	public void run() {
		
		try{
			// update register status for all device.
			updateRegister();
			
			// update characteristic of all no beacon device
			updateDeviceStatus();
			
		}
		catch (Exception e){
			m_logger.warn(i18n.tr("Error in timer execution: ")+e.getMessage());
		}
	}
	
	/**
	 * Update the register status for all devices. 
	 * 
	 * @throws Exception If something goes wrong.
	 */
	private void updateRegister() throws Exception {
		
		// pass all device unsynchronized and execute a register
		Iterator<Device> it = m_devices.iterator();
		while(it.hasNext()){
			Device device = it.next();
			if( false == device.isSyncronized() ){
				device.register();
			}
		}
		
		// unregister device
		Iterator<Device> it2 = m_devices_to_unregister.iterator();
		while(it2.hasNext()){
			Device device = it2.next();
			if( true == device.isRegister() ){
				device.unregister();
			}
			it2.remove();
		}
	}
	
	/**
	 * Update all no beacon device.
	 * 
	 * @throws Exception If something goes wrong.
	 */
	private void updateDeviceStatus() throws Exception {
		
		// Return all device no beacon. So we have to polling the status and update environment if it's changed,
		List<Device> devices = m_devices.stream()
										.filter( r -> r.isRegister() )
										.filter( r -> r.getAccessory().getProtocoltype() == AccessoryProtocolType.JSON_SIMULATION )
										.collect(Collectors.toList());
		
		// Compare the accessory with the new service result, 
		// if is changed, update the environment.
		Iterator<Device> idevice = devices.iterator();
		
		while (idevice.hasNext()) {
			Device device = idevice.next();
			
			if (true == device.isCharacteristicUpdated()) {
				device.updateEnvironment();
				
			}// ENDIF: Do noting.
		}// END WHILE
	}

	/**
	 * Return the list of the devices in the environment.
	 * 
	 * @return Device in the environment.
	 * @see com.connectlife.coreserver.environment.device.DeviceManager#getDevices()
	 */
	@Override
	public List<Device> getDevices() {
		return m_devices;
	}

}
