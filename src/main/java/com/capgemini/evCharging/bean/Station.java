package com.capgemini.evCharging.bean;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

//
//EvCharging Application
//
//Created by The Local host on June 28 2020.
//Copyright © 2020 Local host. All rights reserved.
//

//This class stores the machine / charger stations info, A station is having one or more number if chargers / machines.
@Entity
@Table(uniqueConstraints = {@UniqueConstraint(columnNames={"city","campusLocation"})})
public class Station {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer stationId;
	
	@Column(nullable = false)
	private String city;
	
	@Column(nullable = false)
	private String campusLocation;
	
	
	public Integer getStationId() {
		return stationId;
	}
	public void setStationId(Integer stationId) {
		this.stationId = stationId;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getCampusLocation() {
		return campusLocation;
	}
	public void setCampusLocation(String campusLocation) {
		this.campusLocation = campusLocation;
	}
	@Override
	public String toString() {
		return "Station [stationId=" + stationId + ", city=" + city + ", campusLocation=" + campusLocation + "]";
	}
	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((campusLocation == null) ? 0 : campusLocation.hashCode());
		result = prime * result + ((city == null) ? 0 : city.hashCode());
		result = prime * result + ((stationId == null) ? 0 : stationId.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Station other = (Station) obj;
		if (campusLocation == null) {
			if (other.campusLocation != null)
				return false;
		} else if (!campusLocation.equals(other.campusLocation))
			return false;
		if (city == null) {
			if (other.city != null)
				return false;
		} else if (!city.equals(other.city))
			return false;
		if (stationId == null) {
			if (other.stationId != null)
				return false;
		} else if (!stationId.equals(other.stationId))
			return false;
		return true;
	}
	
	
	
	

}
