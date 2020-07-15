package com.capgemini.evCharging.bean;

import java.time.LocalTime;
import java.sql.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import com.capgemini.evCharging.bean.enums.BookingStatus;


//
//EvCharging Application
//
//Created by The Local host on June 28 2020.
//Copyright © 2020 Local host. All rights reserved.
//

// This Entity class stores all the bookings 
@Entity
public class Booking {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer ticketNo;
	
	
	@ManyToOne(cascade = CascadeType.PERSIST)
	private Machine bookedMachine;
	
	
	@ManyToOne(cascade = CascadeType.ALL)
	private Employee bookingByEmployee;
	
	@Column(nullable = false)
	private Date bookedDate; 
	
	@Column(nullable = false)
	private LocalTime bookingStartTime;
	
	@Column(nullable = false)
	private LocalTime bookingEndTime;
	
	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private BookingStatus status;
	
	public Integer getTicketNo() {
		return ticketNo;
	}
	public void setTicketNo(Integer ticketNo) {
		this.ticketNo = ticketNo;
	}
	public Machine getBookedMachine() {
		return bookedMachine;
	}
	public void setBookedMachine(Machine bookedMachine) {
		this.bookedMachine = bookedMachine;
	}
	public Employee getBookingByEmployee() {
		return bookingByEmployee;
	}
	public void setBookingByEmployee(Employee bookingByEmployee) {
		this.bookingByEmployee = bookingByEmployee;
	}
	public Date getBookedDate() {
		return bookedDate;
	}
	public void setBookedDate(Date bookedDate) {
		this.bookedDate = bookedDate;
	}
	public LocalTime getBookingStartTime() {
		return bookingStartTime;
	}
	public void setBookingStartTime(LocalTime bookingStartTime) {
		this.bookingStartTime = bookingStartTime;
	}
	public LocalTime getBookingEndTime() {
		return bookingEndTime;
	}
	public void setBookingEndTime(LocalTime bookingEndTime) {
		this.bookingEndTime = bookingEndTime;
	}
	public BookingStatus getStatus() {
		return status;
	}
	public void setStatus(BookingStatus status) {
		this.status = status;
	}
	@Override
	public String toString() {
		return "Booking [ticketNo=" + ticketNo + ", bookedMachine=" + bookedMachine + ", bookingByEmployee="
				+ bookingByEmployee + ", bookedDate=" + bookedDate + ", bookingStartTime=" + bookingStartTime
				+ ", bookingEndTime=" + bookingEndTime + ", status=" + status + "]";
	}
	
	
}
