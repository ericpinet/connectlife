/**
 *  Room.java
 *  clapi
 *
 *  Created by ericpinet on 2015-11-08.
 *  Copyright (c) 2015 ConnectLife (Eric Pinet). All rights reserved.
 *
 */
package com.clapi.data;

import java.util.List;
import java.util.Vector;

/**
 * Room.
 * 
 * @author ericpinet
 * <br> 2015-11-08
 */
public class Room extends DataObjDefault implements DataObj {
	
	/**
	 * UID of the room generated by the server.
	 */
	private String uid;
	
	/**
	 * Label of the room.
	 */
	private String label;
	
	/**
	 * List of accessories in the room.
	 */
	private List<Accessory> accessories;
	
	/**
	 * Image of the room.
	 */
	private String imageuid;

	/**
	 * Default constructor. 
	 * 
	 * @param uid Uid of the room.
	 * @param label Label of the room.
	 * @param accessories Accessories of the room.
	 * @param imageuid ImageUID of the room.
	 */
	public Room(String uid, String label, List<Accessory> accessories, String imageuid) {
		super();
		this.uid = uid;
		this.label = label;
		this.accessories = accessories;
		this.imageuid = imageuid;
	}
	
	/**
	 * Default constructor. 
	 * 
	 * @param uid Uid of the room.
	 * @param label Label of the room.
	 */
	public Room(String uid, String label) {
		super();
		this.uid = uid;
		this.label = label;
		this.accessories = new Vector<Accessory>();
		this.imageuid = "";
	}

	/**
	 * @return the uid
	 */
	public String getUid() {
		return uid;
	}

	/**
	 * @param uid the uid to set
	 */
	public void setUid(String uid) {
		this.uid = uid;
	}

	/**
	 * @return the label
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * @param label the label to set
	 */
	public void setLabel(String label) {
		this.label = label;
	}

	/**
	 * @return the accessories
	 */
	public List<Accessory> getAccessories() {
		return accessories;
	}

	/**
	 * @param accessories the accessories to set
	 */
	public void setAccessories(List<Accessory> accessories) {
		this.accessories = accessories;
	}
	
	/**
	 * @param accessory the accessory to add
	 */
	public void addAccessory(Accessory accessory) {
		this.accessories.add(accessory);
	}

	/**
	 * @return the imageuid
	 */
	public String getImageuid() {
		return imageuid;
	}

	/**
	 * @param imageuid the imageuid to set
	 */
	public void setImageuid(String imageuid) {
		this.imageuid = imageuid;
	}
	
	/**
	 * Return children of this object.
	 * 
	 * @return Children of this object.
	 * @see com.clapi.data.DataObj#getChildren()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<DataObj> getChildren() {
		return (List<DataObj>)(Object)getAccessories();
	}
}
