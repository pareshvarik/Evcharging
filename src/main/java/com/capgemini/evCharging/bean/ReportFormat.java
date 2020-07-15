package com.capgemini.evCharging.bean;


//
//EvCharging Application
//
//Created by The Local host on June 28 2020.
//Copyright © 2020 Local host. All rights reserved.
//

//Non Entity Class This acts as a wrapper class for retrieving the machine(s) report 
public class ReportFormat {
	
	private Machine bookedMachine;
	private Long bookingsCount;
	
	public Machine getBookedMachine() {
		return bookedMachine;
	}
	public void setBookedMachine(Machine bookedMachine) {
		this.bookedMachine = bookedMachine;
	}
	public Long getBookingsCount() {
		return bookingsCount;
	}
	public void setBookingsCount(Long bookingsCount) {
		this.bookingsCount = bookingsCount;
	}
	@Override
	public String toString() {
		return "ReportFormat [bookedMachine=" + bookedMachine + ", bookingsCount=" + bookingsCount + "]";
	}
	
	

}
