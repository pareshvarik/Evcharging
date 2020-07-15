package com.capgemini.evCharging.bean;

import java.io.Serializable;
import java.sql.Date;
import java.time.LocalTime;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import com.capgemini.evCharging.bean.enums.MachineStatus;
import com.capgemini.evCharging.bean.enums.MachineType;
import com.capgemini.evCharging.bean.enums.SlotDuration;



//
//EvCharging Application
//
//Created by The Local host on June 28 2020.
//Copyright © 2020 Local host. All rights reserved.
//


//This class stores the Charger/Machine related information
@Entity
public class Machine implements Serializable{
	
	

	private static final long serialVersionUID = 1L;

	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer machineId;
	
	@Column(unique = true)
	private String machineName;
	
	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private MachineType machineType;
	
	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private MachineStatus machineStatus; 
	
	@ManyToOne(cascade = CascadeType.PERSIST)
	private Station machineStation;
	
	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private SlotDuration slotDuration; 
	
	@Column(nullable = false)
	private Date startingDate;
	
	@Column(nullable = false)
	private LocalTime startTime;
	
	@Column(nullable = false)
	private LocalTime endTime;
	
	public Integer getMachineId() {
		return machineId;
	}

	public void setMachineId(Integer machineId) {
		this.machineId = machineId;
	}

	public MachineType getMachineType() {
		return machineType;
	}

	public void setMachineType(MachineType MachineType) {
		this.machineType = MachineType;
	}

	public MachineStatus getMachineStatus() {
		return machineStatus;
	}

	public void setMachineStatus(MachineStatus machineStatus) {
		this.machineStatus = machineStatus;
	}

	public Station getMachineStation() {
		return machineStation;
	}

	public void setMachineStation(Station machineStation) {
		this.machineStation = machineStation;
	}

	public SlotDuration getSlotDuration() {
		return slotDuration;
	}

	public void setSlotDuration(SlotDuration slotDuration) {
		this.slotDuration = slotDuration;
	}

	public Date getStartingDate() {
		return startingDate;
	}

	public void setStartingDate(Date startingDate) {
		this.startingDate = startingDate;
	}

	public LocalTime getStartTime() {
		return startTime;
	}

	public void setStartTime(LocalTime startTime) {
		this.startTime = startTime;
	}

	public LocalTime getEndTime() {
		return endTime;
	}

	public void setEndTime(LocalTime endTime) {
		this.endTime = endTime;
	}

	public String getMachineName() {
		return machineName;
	}

	public void setMachineName(String machineName) {
		this.machineName = machineName;
	}

	@Override
	public String toString() {
		return "Machine [machineId=" + machineId + ", machineName=" + machineName + ", machineType=" + machineType
				+ ", machineStatus=" + machineStatus + ", machineStation=" + machineStation + ", slotDuration="
				+ slotDuration + ", startingDate=" + startingDate + ", startTime=" + startTime + ", endTime=" + endTime
				+ "]";
	}
	
	
	

}
