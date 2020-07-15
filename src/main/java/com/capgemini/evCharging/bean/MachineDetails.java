package com.capgemini.evCharging.bean;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import com.capgemini.evCharging.bean.enums.MachineType;
import com.capgemini.evCharging.bean.enums.SlotDuration;

//
//EvCharging Application
//
//Created by The Local host on June 28 2020.
//Copyright © 2020 Local host. All rights reserved.
//


//Non Entity class stores the machine details in key and value fashion.
public class MachineDetails {
	private HashMap<MachineDetailKey, ArrayList<MachineDetailValue>> machineDetails = new HashMap<MachineDetailKey, ArrayList<MachineDetailValue>>();

	public HashMap<MachineDetailKey, ArrayList<MachineDetailValue>> getMachineDetails() {
		return machineDetails;
	}

	public void setMachineDetails(HashMap<MachineDetailKey, ArrayList<MachineDetailValue>> machineDetails) {
		this.machineDetails = machineDetails;
	}
	
	public MachineDetails(MachineType selectedMachineType) {
		
		// 60 minutes slot duration
		LocalTime startTime = LocalTime.of(0, 0);
		LocalTime endTime = LocalTime.of(1, 0);

		for(int mins = 60;mins <= 24*60; mins+=60) {
		
			machineDetails.put(new MachineDetailKey(startTime, endTime,SlotDuration.SIXTY,selectedMachineType), new ArrayList<MachineDetailValue>());
			
			startTime = startTime.plusMinutes(60);
			endTime = endTime.plusMinutes(60);
			
			
			
		}

		

		//30 minutes slot duration 
		startTime = LocalTime.of(0, 0);
		endTime = LocalTime.of(0, 30);

		for(int mins = 30;mins <= 24*60; mins+= 30) {
			
			machineDetails.put(new MachineDetailKey(startTime, endTime,SlotDuration.THIRTY, selectedMachineType), new ArrayList<MachineDetailValue>());
			

			startTime = startTime.plusMinutes(30);
			endTime = endTime.plusMinutes(30);
			
			
		}


		//15 minutes slot duration 
		startTime = LocalTime.of(0, 0);
		endTime = LocalTime.of(0, 15);
		for(int mins = 15;mins <= 24*60; mins+= 15) {
			
			
			machineDetails.put(new MachineDetailKey(startTime, endTime,SlotDuration.FIFTEEN, selectedMachineType), new ArrayList<MachineDetailValue>());
			startTime = startTime.plusMinutes(15);
			endTime = endTime.plusMinutes(15);
			
		}
		
		System.out.println(machineDetails.keySet().size());
		
	}
	
	public MachineDetails(SlotDuration selectedSlotDuration) {
		
		// for LEVEL1
		LocalTime startTime = LocalTime.of(0, 0);
		LocalTime endTime = startTime.plusMinutes(selectedSlotDuration.getValue());

		for(int mins = selectedSlotDuration.getValue();mins <= 24*60; mins += selectedSlotDuration.getValue()) {
		
			machineDetails.put(new MachineDetailKey(startTime, endTime,selectedSlotDuration,MachineType.LEVEL1), new ArrayList<MachineDetailValue>());
			
			startTime = startTime.plusMinutes(selectedSlotDuration.getValue());
			endTime = endTime.plusMinutes(selectedSlotDuration.getValue());
			
			
			
		}

		

		//for LEVEL2
		startTime = LocalTime.of(0, 0);
		endTime = startTime.plusMinutes(selectedSlotDuration.getValue());

		for(int mins = selectedSlotDuration.getValue();mins <= 24*60; mins += selectedSlotDuration.getValue()) {
		
			machineDetails.put(new MachineDetailKey(startTime, endTime,selectedSlotDuration,MachineType.LEVEL2), new ArrayList<MachineDetailValue>());
			
			startTime = startTime.plusMinutes(selectedSlotDuration.getValue());
			endTime = endTime.plusMinutes(selectedSlotDuration.getValue());
			
			
			
		}



		//for LEVEL3
		startTime = LocalTime.of(0, 0);
		endTime = startTime.plusMinutes(selectedSlotDuration.getValue());

		for(int mins = selectedSlotDuration.getValue();mins <= 24*60; mins += selectedSlotDuration.getValue()) {
		
			machineDetails.put(new MachineDetailKey(startTime, endTime,selectedSlotDuration,MachineType.LEVEL3), new ArrayList<MachineDetailValue>());
			
			startTime = startTime.plusMinutes(selectedSlotDuration.getValue());
			endTime = endTime.plusMinutes(selectedSlotDuration.getValue());
			
			
			
		}

		
		System.out.println(machineDetails.keySet().size());
		
	}

	@Override
	public String toString() {
		return "MachineDetails [machineDetails=" + machineDetails + "]";
	}
	
	
	
}


