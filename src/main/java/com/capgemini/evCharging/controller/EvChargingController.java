 package com.capgemini.evCharging.controller;
import java.time.LocalTime;
import java.sql.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.capgemini.evCharging.bean.Machine;
import com.capgemini.evCharging.bean.MachineDetails;
import com.capgemini.evCharging.bean.ReportFormat;
import com.capgemini.evCharging.bean.Station;
import com.capgemini.evCharging.bean.enums.MachineType;
import com.capgemini.evCharging.bean.enums.SlotDuration;
import com.capgemini.evCharging.bean.Booking;
import com.capgemini.evCharging.bean.Employee;
import com.capgemini.evCharging.exception.EvChargingException;
import com.capgemini.evCharging.service.EvChargingService;










//@RestController
@RestController
//@RequestMapping("/charging")
@CrossOrigin(origins="http://localhost:4200")
//@CrossOrigin(origins = "http://localhost:4200", allowedHeaders = "*")
public class EvChargingController {

	@Autowired
	EvChargingService chargingService;
	
	
	//User Actions : Employee  + Admin
	
	//This handles the post request for user registration with an extra value of isAdmin boolean depicting whether admin is registered or employee
	@PostMapping("/register/employee/{password}/{isAdmin}")
	public Employee registerEmployee(@RequestBody Employee employee, @PathVariable("password") String password, @PathVariable("isAdmin") Boolean isAdmin) throws EvChargingException   {
		return chargingService.registerEmployee(employee, password,isAdmin);
	}
	
	//This handles the get mapping for signing in request for both employee and admin
	@GetMapping("/login/{email}/{password}")
	public Employee loginUser(@PathVariable String email, @PathVariable String password)throws EvChargingException {
		return chargingService.areCredentialsMatched(email, password);
	}
	
	//This handles the get request for fetching a list of all stations
	@GetMapping("/get/stations")
	public List<Station> getAllStations() {
		
		return chargingService.getStations();
		
	}
	
	//Employee Actions
	
	//This handles the get request for fetching the next available date for booking
	@GetMapping("/get/nextDate/{forMachineType}/{stationId}") 
	public Date getNextAvailableBookingDate(@PathVariable MachineType forMachineType, @PathVariable Integer stationId) throws EvChargingException {
		return chargingService.getNextAvailableBookingDate(forMachineType, stationId);
	}
	
	
	//This handles the get request for fetching the machine details, whether they are booked by or not and if booked by whom, by machine type.
	@GetMapping("/get/bookingDetailByType/{selectedDate}/{selectedMachineType}/{stationId}")
	public MachineDetails getMachineBookingDetailByType(@PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") java.util.Date selectedDate, @PathVariable MachineType selectedMachineType, @PathVariable Integer stationId) throws EvChargingException {
		
		Date selectedSqlDate = new Date(selectedDate.getTime());
		System.out.println(selectedSqlDate);
		
		return chargingService.getMachineBookingDetail(selectedSqlDate, selectedMachineType, stationId);
	}
	
	//This handles the get request for getting the all bookings done by employee. 
	@GetMapping("/get/allBookings/{empId}")
	public List<Booking> getEmployeeAllBookings(@PathVariable Integer empId) throws EvChargingException {
		return chargingService.getEmployeeAllBookings(empId);
	}
	
	//This handles the get request for fetching the future bookings done by employee.
	@GetMapping("/get/currentBookings/{empId}")
	public List<Booking> getEmployeeCurrentBookings(@PathVariable Integer empId) throws EvChargingException {
		return chargingService.getEmployeeCurrentBookings(empId);
	}
	
	//This handles the post request for booking machine, entering a record in Booking table.
	@PostMapping("/bookMachine/{bookedDate}/{bookingStartTime}/{machineId}/{employeeId}") 
	public List<Booking> bookMachine(@PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") java.util.Date bookedDate,@PathVariable  @DateTimeFormat(pattern = "HH:mm:ss") LocalTime bookingStartTime,@PathVariable Integer machineId,@PathVariable Integer employeeId) throws EvChargingException {
		Date bookedSqlDate = new Date(bookedDate.getTime());
		return chargingService.bookMachine(bookedSqlDate, bookingStartTime, machineId, employeeId);
	}
	
	//This handles the put request for changing the status of Booking to CANCELLED
	@PutMapping("/cancelBooking/{ticketNo}") 
	public List<Booking> cancelBooking(@PathVariable Integer ticketNo) throws EvChargingException {
		return chargingService.cancelBooking(ticketNo);
	}
	
	// This handles the put request for rescheduling the booking, changing the current booking status to RESCHEDULED and creating a new booking
	@PutMapping("/rescheduleBooking/{rescheduleTicketNo}/{rescheduledBookedDate}/{rescheduledBookingStartTiming}/{machineId}/{employeeId}")
	public List<Booking> rescheduleBooking(@PathVariable Integer rescheduleTicketNo,@PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") java.util.Date rescheduledBookedDate,@PathVariable @DateTimeFormat(pattern = "HH:mm:ss") LocalTime rescheduledBookingStartTiming,@PathVariable Integer machineId,@PathVariable Integer employeeId) throws EvChargingException {
		
		return chargingService.rescheduleBooking(rescheduleTicketNo, new Date(rescheduledBookedDate.getTime()), rescheduledBookingStartTiming, machineId, employeeId);
	}
	
	
	//Admin actions
	
	// This handles the get request for  for fetching the machine details, whether they are booked by or not and if booked by whom, by machine slot duration.
	@GetMapping("/get/bookingDetailBySlot/{selectedDate}/{selectedSlotDuration}/{stationId}")
	public MachineDetails getMachineBookingDetailBySlot(@PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") java.util.Date selectedDate, @PathVariable SlotDuration selectedSlotDuration, @PathVariable Integer stationId) throws EvChargingException {
		return chargingService.getMachineBookingDetail(new Date(selectedDate.getTime()), selectedSlotDuration, stationId);
	}
	
	//This handles the post request for adding new machines to the existing station.
	@PostMapping("/add/machines/{stationId}")
	public List<Machine> addMachines(@PathVariable("stationId") Integer stationId, @RequestBody List<Machine> machines) throws EvChargingException {
		return chargingService.addMachines(stationId, machines);
	}
	

	//This handles the delete request for changing the status of machine to REMOVED, so that employee may see the past bookings
	@DeleteMapping("/remove/machine/{machineId}")
	public List<Machine> removeMachine(@PathVariable Integer machineId) throws EvChargingException {
		return chargingService.removeMachine(machineId);
	}
	
	
	//This handles the put request for halting machine with new start date
	@PutMapping("/haltMachine/{machineId}/{newStartDate}")
	public Machine haltMachine(@PathVariable Integer machineId,@PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") java.util.Date newStartDate) throws EvChargingException {
		return chargingService.haltMachine(machineId, new Date(newStartDate.getTime()));
	}
	
	//This handles the halting request for normal maintenance of machine with new start date, new start time and new end time
	@PutMapping("/haltMachine/normalMaintenance/{machineId}/{newStartDate}/{newStartTime}/{newEndTime}") 
	public Machine haltMachine(@PathVariable Integer machineId,@PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") java.util.Date newStartDate,@PathVariable @DateTimeFormat(pattern = "HH:mm:ss") LocalTime newStartTime,@PathVariable @DateTimeFormat(pattern = "HH:mm:ss") LocalTime newEndTime) throws EvChargingException{
		return chargingService.haltMachine(machineId, new Date(newStartDate.getTime()), newStartTime, newEndTime);
	}
	
	//This handles the put request for resuming the halted machine
	@PutMapping("/resumeMachine/{machineId}")
	public Machine resumeMachine(@PathVariable Integer machineId) throws EvChargingException {
		return chargingService.resumeMachine(machineId);
	}
	
	//This handles the put request for modifying machine with whole updated machine object passed in Request Body
	@PutMapping("/modifyMachine")
	public Machine modifyMachine(@RequestBody Machine modifiedMachine) throws EvChargingException {
		return chargingService.modifyMachine(modifiedMachine);
	}
	
	//This handles the get request for report generation for all the bookings done at particular station from start to end date
	@GetMapping("/generateReport/{fromDate}/{toDate}/{stationId}")
	public List<ReportFormat> generateBookingsReport(@PathVariable  @DateTimeFormat(pattern = "yyyy-MM-dd") java.util.Date fromDate,@PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") java.util.Date toDate,@PathVariable Integer stationId) {
		return chargingService.generateBookingsReport(stationId,new Date(fromDate.getTime()), new Date(toDate.getTime()));
	}
	
	//This handles the get request for report generation for all the bookings done for particular machine from start to end date 
	@GetMapping("/generateReport/machine/{fromDate}/{toDate}/{machineId}")
	public List<Booking> generateMachineBookingsReport(@PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") java.util.Date fromDate,@PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") java.util.Date toDate,@PathVariable Integer machineId) {
		return chargingService.generateMachineBookingsReport(machineId, new Date(fromDate.getTime()), new Date(toDate.getTime()));
	}
	
	//This handles the post request for adding the charging station 
	@PostMapping("/add/station/{city}/{campusLocation}")
	public List<Station> addStation(@PathVariable String city,@PathVariable String campusLocation){
		return chargingService.addStation(city, campusLocation);
	}
}
