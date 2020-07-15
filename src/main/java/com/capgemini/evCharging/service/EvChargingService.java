package com.capgemini.evCharging.service;

import java.time.LocalTime;
import java.sql.Date;
import java.util.List;
import com.capgemini.evCharging.bean.Booking;
import com.capgemini.evCharging.bean.Employee;
import com.capgemini.evCharging.bean.Machine;
import com.capgemini.evCharging.bean.MachineDetails;
import com.capgemini.evCharging.bean.ReportFormat;
import com.capgemini.evCharging.bean.Station;
import com.capgemini.evCharging.bean.enums.MachineType;
import com.capgemini.evCharging.bean.enums.SlotDuration;
import com.capgemini.evCharging.exception.EvChargingException;


//
//EvCharging Application
//
//Created by The Local host on June 28 2020.
//Copyright © 2020 Local host. All rights reserved.
//
public interface EvChargingService {
	
	//Registration and login
	/**
	 * @param mailId
	 * @param password
	 * @return
	 * @throws EvChargingException
	 */
	public Employee areCredentialsMatched(String mailId,String password) throws EvChargingException ; 
	
	/**
	 * @param emp
	 * @param password
	 * @param isAdmin
	 * @return
	 * @throws EvChargingException
	 */
	public Employee registerEmployee(Employee emp, String password,Boolean isAdmin) throws EvChargingException;

	/**
	 * @return
	 */
	public List<Station> getStations();
	
	//Employee actions
	
	/**
	 * @param selectedMachineType
	 * @param selectedStationId
	 * @return
	 * @throws EvChargingException
	 */
	public Date getNextAvailableBookingDate(MachineType selectedMachineType, Integer selectedStationId) throws EvChargingException;
	
	/**
	 * @param selectedDate
	 * @param selectedMachineType
	 * @param stationId
	 * @return
	 * @throws EvChargingException
	 */
	public MachineDetails getMachineBookingDetail(Date selectedDate, MachineType selectedMachineType, Integer stationId) throws EvChargingException;
	
	/**
	 * @param bookedDate
	 * @param bookedTiming
	 * @param machineId
	 * @param employeeId
	 * @return
	 * @throws EvChargingException
	 */
	public List<Booking> bookMachine(Date bookedDate, LocalTime bookedTiming, Integer machineId, Integer employeeId) throws EvChargingException;

	/**
	 * @param employeeId
	 * @return
	 * @throws EvChargingException
	 */
	public List<Booking> getEmployeeAllBookings(Integer employeeId) throws EvChargingException;
	
	/**
	 * @param employeeId
	 * @return
	 * @throws EvChargingException
	 */
	public List<Booking> getEmployeeCurrentBookings(Integer employeeId) throws EvChargingException;
	
	/**
	 * @param ticketNo
	 * @return
	 * @throws EvChargingException
	 */
	public List<Booking> cancelBooking(Integer ticketNo) throws EvChargingException;
	
	/**
	 * @param ticketNo
	 * @param bookedDate
	 * @param bookingStartTiming
	 * @param machineId
	 * @param employeeId
	 * @return
	 * @throws EvChargingException
	 */
	public List<Booking> rescheduleBooking(Integer ticketNo,Date bookedDate, LocalTime bookingStartTiming, Integer machineId, Integer employeeId) throws EvChargingException;
	
	
	// Admin actions
	
	/**
	 * @param selectedDate
	 * @param selectedDuration
	 * @param stationId
	 * @return
	 * @throws EvChargingException
	 */
	public MachineDetails getMachineBookingDetail(Date selectedDate, SlotDuration selectedDuration, Integer stationId) throws EvChargingException;
	
	/**
	 * @param stationId
	 * @param machines
	 * @return
	 * @throws EvChargingException
	 */
	public List<Machine> addMachines(Integer stationId, List<Machine> machines) throws EvChargingException;
	
	/**
	 * @param machineId
	 * @return
	 * @throws EvChargingException
	 */
	public List<Machine> removeMachine(Integer machineId) throws EvChargingException;
	
	/**
	 * @param machineId
	 * @param newStartDate
	 * @param newStartTime
	 * @param newEndTime
	 * @return
	 * @throws EvChargingException
	 */
	public Machine haltMachine(Integer machineId, Date newStartDate, LocalTime newStartTime, LocalTime newEndTime) throws EvChargingException; // for normal maintenance startDate += startDate 
	
	/**
	 * @param machineId
	 * @param newStartDate
	 * @return
	 * @throws EvChargingException
	 */
	public Machine haltMachine(Integer machineId, Date newStartDate) throws EvChargingException; // for normal maintenance startDate += startDate 
	
	/**
	 * @param machineId
	 * @return
	 * @throws EvChargingException
	 */
	public Machine resumeMachine(Integer machineId) throws EvChargingException;
	
	/**
	 * @param modifiedMachine
	 * @return
	 * @throws EvChargingException
	 */
	public Machine modifyMachine(Machine modifiedMachine) throws EvChargingException;
	
	/**
	 * @param stationId
	 * @param fromDate
	 * @param toDate
	 * @return
	 */
	public List<ReportFormat> generateBookingsReport(Integer stationId,Date fromDate, Date toDate);
	
	/**
	 * @param machineId
	 * @param fromDate
	 * @param toDate
	 * @return
	 */
	public List<Booking> generateMachineBookingsReport(Integer machineId,Date fromDate, Date toDate);
	
	/**
	 * @param city
	 * @param campusLocation
	 * @return
	 */
	public List<Station> addStation(String city,String campusLocation);
	
	
}
