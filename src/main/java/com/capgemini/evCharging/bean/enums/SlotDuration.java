package com.capgemini.evCharging.bean.enums;

//
//EvCharging Application
//
//Created by The Local host on June 28 2020.
//Copyright © 2020 Local host. All rights reserved.
//


//Restricting the slot duration with some predefined values
public enum SlotDuration {
	
	FIFTEEN(15), THIRTY(30), SIXTY(60) ;
	
	private Integer value;
	
	
	SlotDuration(Integer value) {
		this.value = value;
	}
	
	public int getValue() {
	     return value;
	 }

	

}
