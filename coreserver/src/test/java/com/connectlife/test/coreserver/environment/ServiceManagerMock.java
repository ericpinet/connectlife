/**
 *  ServiceManagerMock.java
 *  coreserver
 *
 *  Created by ericpinet on 2016-01-23.
 *  Copyright (c) 2016 ConnectLife (Eric Pinet). All rights reserved.
 *
 */
package com.connectlife.test.coreserver.environment;

import java.util.List;

import com.connectlife.coreserver.environment.device.Device;
import com.connectlife.coreserver.environment.device.DeviceManager;

/**
 * 
 * 
 * @author ericpinet
 * <br> 2016-01-23
 */
public class ServiceManagerMock implements DeviceManager {

	/**
	 * @return
	 * @see com.connectlife.coreserver.environment.device.DeviceManager#init()
	 */
	@Override
	public boolean init() {
		return true;
	}

	/**
	 * @return
	 * @see com.connectlife.coreserver.environment.device.DeviceManager#isInit()
	 */
	@Override
	public boolean isInit() {
		return true;
	}

	/**
	 * 
	 * @see com.connectlife.coreserver.environment.device.DeviceManager#unInit()
	 */
	@Override
	public void unInit() {
	}

	/**
	 * @return
	 * @see com.connectlife.coreserver.environment.device.DeviceManager#getDevices()
	 */
	@Override
	public List<Device> getDevices() {
		return null;
	}

	/**
	 * 
	 * @see com.connectlife.coreserver.environment.device.DeviceManager#forceSynchronizationOfAllDevices()
	 */
	@Override
	public void forceSynchronizationOfAllDevices() {
	}

}
