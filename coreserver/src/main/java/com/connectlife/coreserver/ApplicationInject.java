/**
 *  ApplicationInject.java
 *  coreserver
 *
 *  Created by ericpinet on 2015-10-10.
 *  Copyright (c) 2015 ConnectLife (Eric Pinet). All rights reserved.
 *
 */
package com.connectlife.coreserver;

// external
import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.clapi.protocol.CLApiGrpc;

import com.connectlife.coreserver.apiserver.Api;
import com.connectlife.coreserver.apiserver.ApiGrpc;
import com.connectlife.coreserver.apiserver.ApiProcessor;
import com.connectlife.coreserver.config.Config;
import com.connectlife.coreserver.config.ConfigSqlite;
import com.connectlife.coreserver.config.SqliteSettings;
import com.connectlife.coreserver.config.SqliteSettingsApp;
import com.connectlife.coreserver.console.Console;
import com.connectlife.coreserver.console.ConsoleSSH;
import com.connectlife.coreserver.environment.Environment;
import com.connectlife.coreserver.environment.EnvironmentManager;
import com.connectlife.coreserver.environment.asset.AssetManager;
import com.connectlife.coreserver.environment.asset.AssetMngr;
import com.connectlife.coreserver.environment.data.DataManager;
import com.connectlife.coreserver.environment.data.DataManagerNeo4j;
import com.connectlife.coreserver.environment.device.DeviceManager;
import com.connectlife.coreserver.environment.device.DeviceMngr;
import com.connectlife.coreserver.environment.discover.DiscoveryJmdns;
import com.connectlife.coreserver.environment.discover.DiscoveryService;
import com.connectlife.coreserver.gpio.Gpio;
import com.connectlife.coreserver.gpio.RaspberryPiGpio;
import com.connectlife.coreserver.gpio.SimulatorGpio;
import com.connectlife.coreserver.tools.os.OperatingSystem;
import com.connectlife.coreserver.webserver.WebServer;
import com.connectlife.coreserver.webserver.WebServerJetty;

/**
 * Google Guice injection module to create an Application Class.
 * 
 * @author ericpinet
 * <br> 2015-10-10
 */
public class ApplicationInject extends AbstractModule {

	/**
	 * 
	 * @see com.google.inject.AbstractModule#configure()
	 */
	@Override
	protected void configure() {
		
		// Config
		bind(SqliteSettings.class).to(SqliteSettingsApp.class);
		bind(Config.class).to(ConfigSqlite.class).in(Singleton.class);
		
		// Environment
		bind(Environment.class).to(EnvironmentManager.class).in(Singleton.class);
		bind(DiscoveryService.class).to(DiscoveryJmdns.class);
		//bind(DiscoveryService.class).to(DiscoveryZigbee.class);
		bind(DeviceManager.class).to(DeviceMngr.class);
		bind(DataManager.class).to(DataManagerNeo4j.class);
		bind(AssetManager.class).to(AssetMngr.class);
		
		// Api 
		bind(Api.class).to(ApiGrpc.class);
		bind(CLApiGrpc.CLApi.class).to(ApiProcessor.class);
		
		// Console
		bind(Console.class).to(ConsoleSSH.class);
		
		// GPIO
		if(OperatingSystem.isLinux()){
			bind(Gpio.class).to(RaspberryPiGpio.class);	
		}
		else{
			bind(Gpio.class).to(SimulatorGpio.class);
		}
		
		// WebServer
		bind(WebServer.class).to(WebServerJetty.class);
	}

}
