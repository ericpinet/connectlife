/**
 *  ConfigItem.java
 *  coreserver
 *
 *  Created by Eric Pinet <pineri01@gmail.com> on 2015-09-07.
 *  Copyright (c) 2015 ConnectLife (Eric Pinet). All rights reserved.
 *
 */
package com.connectlife.coreserver.config;

/**
 * Configuration use in the application
 * 
 * @author Eric Pinet (pineri01@gmail.com)
 * <br> 2015-09-07
 */
public class ConfigItem {
	
	/**
	 * Section configuration name.
	 */
	private String m_section;
	
	/**
	 * Item configuration name.
	 */
	private String m_item;
	
	/**
	 * configuration type
	 */
	private ConfigType m_type;
	
	/**
	 * Value of configuration if it's string
	 */
	private String m_string_value;
	
	/**
	 * Value of configuration if it's integer
	 */
	private int m_integer_value;

	/**
	 * Enum of configuration type.
	 */
	public enum ConfigType{
		STRING, INTEGER
	}
	
	/**
	 * Constructor for configuration with string value.
	 * @param _section	ConfigItem section.
	 * @param _item		ConfigItem item.
	 * @param _value	ConfigItem value.
	 */
	public ConfigItem(String _section, String _item, String _value){
		m_section = _section;
		m_item = _item;
		m_string_value = _value;
		m_integer_value = 0;
		m_type = ConfigType.STRING;
	}
	
	/**
	 * Constructor for configuration with integer value.
	 * @param _section	ConfigItem section.
	 * @param _item		ConfigItem item.
	 * @param _value	ConfigItem value.
	 */
	public ConfigItem(String _section, String _item, Integer _value){
		m_section = _section;
		m_item = _item;
		m_integer_value = _value;
		m_string_value = _value.toString();
		m_type = ConfigType.INTEGER;
	}
	
	/**
	 * Return String value of the configuration.
	 * @return String value of this configuration.
	 */
	public String getStringValue(){
		return m_string_value;
	}
	
	/**
	 * Return Integer value of the configuration.
	 * @return Integer value of this configuration.
	 */
	public Integer getIntegerValue(){
		return m_integer_value;
	}
	
	/**
	 * Convert the value of the configuration to string and return it.
	 * @return Value of this configuration converted in string.
	 */
	public String getValueToString(){
		if(m_type == ConfigType.INTEGER){
			return getIntegerValue().toString();
		}
		else{
			return getStringValue();
		}
	}
	
	/**
	 * Return the section of the configuration.
	 * @return Section name.
	 */
	public String getSection(){
		return m_section;
	}
	
	/**
	 * Return the item of the configuration.
	 * @return Item name.
	 */
	public String getItem(){
		return m_item;
	}
	
	/**
	 * Return the type of the configuration.
	 * @return Type of this configuration.
	 */
	public ConfigType getType(){
		return m_type;
	}

}
