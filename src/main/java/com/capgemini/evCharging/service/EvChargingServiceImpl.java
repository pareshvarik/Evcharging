package com.capgemini.evCharging.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.sql.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.capgemini.evCharging.bean.Booking;
import com.capgemini.evCharging.bean.Credential;
import com.capgemini.evCharging.bean.Employee;
import com.capgemini.evCharging.bean.Machine;
import com.capgemini.evCharging.bean.MachineDetailKey;
import com.capgemini.evCharging.bean.MachineDetailValue;
import com.capgemini.evCharging.bean.MachineDetails;
import com.capgemini.evCharging.bean.ReportFormat;
import com.capgemini.evCharging.bean.Station;
import com.capgemini.evCharging.bean.enums.BookingStatus;
import com.capgemini.evCharging.bean.enums.MachineDetailStatus;
import com.capgemini.evCharging.bean.enums.MachineStatus;
import com.capgemini.evCharging.bean.enums.MachineType;
import com.capgemini.evCharging.bean.enums.SlotDuration;
import com.capgemini.evCharging.dao.BookingDao;
import com.capgemini.evCharging.dao.CredentialDao;
import com.capgemini.evCharging.dao.EmployeeDao;
import com.capgemini.evCharging.dao.MachineDao;
import com.capgemini.evCharging.dao.StationDao;
import com.capgemini.evCharging.exception.EvChargingException;
import com.capgemini.evCharging.util.Utility;

//
//EvCharging Application
//
//Created by The Local host on June 28 2020.
//Copyright © 2020 Local host. All rights reserved.
//
@Service
public class EvChargingServiceImpl implements EvChargingService {


	@Autowired
	EmployeeDao employeeRepo;

	@Autowired
	CredentialDao credentialRepo;

	@Autowired
	StationDao stationRepo;

	@Autowired
	MachineDao machineRepo;

	@Autowired
	BookingDao bookingRepo;


	/* (non-Javadoc)
	 * @see com.capgemini.evCharging.service.EvChargingService#areCredentialsMatched(java.lang.String, java.lang.String)
	 */
	
	@Override
	public Employee areCredentialsMatched(String mailId, String password) throws EvChargingException {

		try {
			Optional<Credential> optionalCredential = credentialRepo.findByMailId(mailId);
			if (optionalCredential.isPresent()) {
				Credential credential = optionalCredential.get();
				String hashUserPassword = HashAlgorithmService.hashedPassword(password, credential.getSaltArray());
				if (hashUserPassword.equals(credential.getHashedPassword())) {
					return employeeRepo.findByMailId(mailId).get();
				}
				throw new EvChargingException("Password mismatch");

			} else {
				throw new EvChargingException("Mail Id is not registered!");
			}

		} catch (Exception exception) {
			throw new EvChargingException(exception.getMessage());
			
		}


	}


	/* (non-Javadoc)
	 * @see com.capgemini.evCharging.service.EvChargingService#registerEmployee(com.capgemini.evCharging.bean.Employee, java.lang.String, java.lang.Boolean)
	 */
	@Override
	public Employee registerEmployee(Employee emp, String password, Boolean isAdmin) throws EvChargingException {

		try {
			Optional<Station> optionalStation = stationRepo.checkIfStationExists(emp.getEmployeeStation().getCity(), emp.getEmployeeStation().getCampusLocation()) ;
			if(optionalStation.isEmpty()) {
				throw new EvChargingException("No such charging station exists, add the station first");
			}
			employeeRepo.save(emp);
			Employee employee = employeeRepo.findByMailId(emp.getMailId()).get();
			Credential credential = new Credential();
			credential.setEmployeeId(employee.getEmployeeId());
			credential.setMailId(emp.getMailId());
			credential.setIsAdmin(isAdmin);

			byte[] salt = HashAlgorithmService.createSalt();
			String hashedPassword = HashAlgorithmService.hashedPassword(password, salt);
			credential.setHashedPassword(hashedPassword);
			credential.setSaltArray(salt);
			System.out.println(credential);
			System.out.println(employee);
			
			credentialRepo.save(credential);

			return employee;
		} catch (Exception exception) {

			throw new EvChargingException(exception.getMessage());
		}

	}



	/* (non-Javadoc)
	 * @see com.capgemini.evCharging.service.EvChargingService#getStations()
	 */
	@Override
	public List<Station> getStations() {
		return stationRepo.findAll();
	}

	/**
	 * @param selectedDate
	 * @param currentTime
	 * @param startTime
	 * @param slotDuration
	 * @return
	 */
	public LocalTime getNearestAvailableStartTime(Date selectedDate, LocalTime currentTime, LocalTime startTime, SlotDuration slotDuration) {
		Date currentDate = new Date(System.currentTimeMillis());
		if(selectedDate.after(currentDate)) {
			return startTime;
		} 
		while(!startTime.isAfter(currentTime)) {
			startTime = startTime.plusMinutes(slotDuration.getValue());
		}
		return startTime;
	}

	/**
	 * @param selectedMachineType
	 * @param stationId
	 * @param selectedDate
	 * @param currentTime
	 * @return
	 */
	public Integer getPossibleNumberOfBookings(MachineType selectedMachineType, Integer stationId,Date selectedDate,LocalTime currentTime) {
		
		List<Machine> machines = getActiveMachinesOfTypeAndStation(selectedMachineType, stationId, selectedDate);
		
		LocalTime endOfTheDayTime = LocalTime.of(23, 59, 59);
		
		
		System.out.println(machines);
		Integer possibleBookings = 0;
		for(Machine machine: machines) {
			
			 LocalTime startTime = getNearestAvailableStartTime(selectedDate, currentTime, machine.getStartTime(), machine.getSlotDuration());
			 
			 if(!startTime.isAfter(machine.getEndTime())) {
				
				 possibleBookings += ((machine.getEndTime().toSecondOfDay()  - startTime.toSecondOfDay()) / (machine.getSlotDuration().getValue() * 60));
					if(endOfTheDayTime.equals(machine.getEndTime())) {
						possibleBookings ++;
					}
					
			 }
			
		}
		return possibleBookings;
	}

	/* (non-Javadoc)
	 * @see com.capgemini.evCharging.service.EvChargingService#getNextAvailableBookingDate(com.capgemini.evCharging.bean.enums.MachineType, java.lang.Integer)
	 */
	@Override
	public Date getNextAvailableBookingDate(MachineType selectedMachineType, Integer selectedStationId) throws EvChargingException {
		
		LocalDate currentDate = LocalDate.now();
		LocalDate selectedDate;
		Date sqlFormattedDate = new Date(System.currentTimeMillis());
		Boolean isFound = false;
		
		LocalTime currentTime = LocalTime.now();
			
		for(selectedDate = currentDate; !isFound ; selectedDate =  selectedDate.plusDays(1)) {
			
			sqlFormattedDate = Date.valueOf(selectedDate);
			Integer currentBookings =  bookingRepo.getBookingsAtStationOnDateWithType(selectedStationId, sqlFormattedDate, selectedMachineType,currentTime);
			
			Integer possibleBookings = getPossibleNumberOfBookings(selectedMachineType, selectedStationId, sqlFormattedDate,currentTime);
			System.out.println("Current: " + currentBookings + " " + "possibleBookings" + " " + possibleBookings );
			if(currentBookings < possibleBookings) {
				isFound = true;
				
			} else if(currentBookings > possibleBookings) {
				throw new EvChargingException("Current Active Bookings " + currentBookings + " at "+ selectedStationId + " station with selected machine type "+ selectedMachineType + " on " + selectedDate + " can't be greater than possible bookings " + possibleBookings);
			} 
			
			
		}
		
		return sqlFormattedDate;
		
	}



	/**
	 * @param selectedMachineType
	 * @param stationId
	 * @param selectedDate
	 * @return
	 */
	public List<Machine> getActiveMachinesOfTypeAndStation(MachineType selectedMachineType, Integer stationId,Date selectedDate) {
		return machineRepo.getActiveMachinesOfStationAndType(selectedMachineType, stationId, MachineStatus.ACTIVE, selectedDate);

	}


	/**
	 * @param machineId
	 * @param selectedDate
	 * @return
	 */
	public List<Booking> getBookingsOfMachine(Integer machineId, Date selectedDate) {
		
		return bookingRepo.getBookingsOfMachine(machineId, selectedDate, BookingStatus.BOOKED);
	}


	/**
	 * @param booking
	 * @param machineDetails
	 * @return
	 * @throws EvChargingException
	 */
	public MachineDetails updateMachineBookingDetail(Booking booking, MachineDetails machineDetails) throws EvChargingException{

		int mins = (booking.getBookingEndTime().toSecondOfDay() - booking.getBookingStartTime().toSecondOfDay()) / 60;
		SlotDuration slotDuration;
		if(mins == 60) {
			slotDuration = SlotDuration.SIXTY;
		} else if(mins == 30) {
			slotDuration = SlotDuration.THIRTY;
		} else if(mins == 15) {
			slotDuration = SlotDuration.FIFTEEN;
		} else {
			throw new EvChargingException("Booking slot duration is not applicable for " + mins);
		}
		MachineDetailKey detailKey = new MachineDetailKey(booking.getBookingStartTime(), booking.getBookingEndTime(),slotDuration,booking.getBookedMachine().getMachineType());
		
		ArrayList<MachineDetailValue> machineDetailValues = machineDetails.getMachineDetails().get(detailKey);
		
		for(MachineDetailValue detailValue : machineDetailValues) {

			if(detailValue.getMachineId() == booking.getBookedMachine().getMachineId()) {
				machineDetailValues.remove(detailValue);
				detailValue.setBookedByEmployeeId(booking.getBookingByEmployee().getEmployeeId()) ;
				detailValue.setStatus(MachineDetailStatus.BOOKED);
				machineDetailValues.add(detailValue);
				machineDetails.getMachineDetails().put(detailKey, machineDetailValues);
			}
		}
		return machineDetails;
	}


	/**
	 * @throws EvChargingException
	 */
	public void getMachinesWhichCanResume() throws EvChargingException {
		
		Date currentDate = new Date(System.currentTimeMillis());
		List<Machine> resumeMachines =  machineRepo.getMachinesWhichCanResume(currentDate, MachineStatus.HALTED);
		for(Machine machine : resumeMachines) {
			resumeMachine(machine.getMachineId());
		}
		
	}
	
	/* (non-Javadoc)
	 * @see com.capgemini.evCharging.service.EvChargingService#getMachineBookingDetail(java.sql.Date, com.capgemini.evCharging.bean.enums.MachineType, java.lang.Integer)
	 */
	@Override
	public MachineDetails getMachineBookingDetail(Date selectedDate, MachineType selectedMachineType, Integer stationId) throws EvChargingException {

		getMachinesWhichCanResume();
		List<Machine> machines =  getActiveMachinesOfTypeAndStation(selectedMachineType,stationId,selectedDate);
		MachineDetails machineDetails = new MachineDetails(selectedMachineType);
		machineDetails =  Utility.utilityObject.populateMachineDetails(machineDetails,machines);

		for (Machine machine : machines) {
			List<Booking> bookings = getBookingsOfMachine(machine.getMachineId(), selectedDate);

			for(Booking booking : bookings) {

			
				machineDetails = updateMachineBookingDetail(booking, machineDetails);

			}
		}

		return machineDetails;
	}




	/* (non-Javadoc)
	 * @see com.capgemini.evCharging.service.EvChargingService#bookMachine(java.sql.Date, java.time.LocalTime, java.lang.Integer, java.lang.Integer)
	 */
	@Override
	public List<Booking> bookMachine(Date bookedDate, LocalTime bookingStartTiming, Integer machineId, Integer employeeId) throws EvChargingException {

		
		Machine bookedMachine = Utility.utilityObject.getMachineFromMachineId(machineId, machineRepo);
		
		if(bookingStartTiming.isBefore(bookedMachine.getStartTime()) || bookingStartTiming.plusMinutes(bookedMachine.getSlotDuration().getValue()).isAfter(bookedMachine.getEndTime())) {
			throw new EvChargingException("Machine can't be booked after/ before its available timings " + bookedMachine.getStartTime() + " to " + bookedMachine.getEndTime());
		}
		
		if(bookedMachine.getMachineStatus() == MachineStatus.HALTED) {
			throw new EvChargingException("Machine with id " + bookedMachine.getMachineId() + " is in halt state");
		}
		
		Optional<Employee> optionalBookedByEmployee = employeeRepo.findById(employeeId);
		if(optionalBookedByEmployee.isEmpty()) {
			throw new EvChargingException("Employee with empId " + employeeId + " not present");
		}
		Optional<Booking> checkedBooking = bookingRepo.checkIfMachineIsNotBooked(machineId, bookedDate, bookingStartTiming);
		if(checkedBooking.isPresent() && checkedBooking.get().getStatus() == BookingStatus.BOOKED) {
			throw new EvChargingException("Machine is already booked by employee with empId " + checkedBooking.get().getBookingByEmployee().getEmployeeId());
		}
		
		
		
		Booking booking = new Booking();
		booking.setBookedMachine(bookedMachine);
		booking.setBookedDate(bookedDate);
		booking.setBookingStartTime(bookingStartTiming);
		booking.setBookingEndTime(bookingStartTiming.plusMinutes(bookedMachine.getSlotDuration().getValue()));
		booking.setBookingByEmployee(optionalBookedByEmployee.get());
		booking.setStatus(BookingStatus.BOOKED);
		bookingRepo.save(booking);

		return bookingRepo.findAll();


	}

	/* (non-Javadoc)
	 * @see com.capgemini.evCharging.service.EvChargingService#getEmployeeAllBookings(java.lang.Integer)
	 */
	@Override
	public List<Booking> getEmployeeAllBookings(Integer employeeId) throws EvChargingException {
		List<Booking> bookings = bookingRepo.getAllBookingsByEmployee(employeeId);

		if(bookings.isEmpty()) {
			throw new EvChargingException("User with id " + employeeId + " has no booking");
		}

		return bookings;
	}
	
	/* (non-Javadoc)
	 * @see com.capgemini.evCharging.service.EvChargingService#getEmployeeCurrentBookings(java.lang.Integer)
	 */
	@Override
	public List<Booking> getEmployeeCurrentBookings(Integer employeeId) throws EvChargingException {
		
			Date currentDate = new Date(System.currentTimeMillis());
			//String quotedDate = "\'" +  currentDate.toString() + "\'";
			LocalTime currentTime = LocalTime.parse(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm")));
			//String quotedTime = "\'" + currentTime.toString() + "\'"; 
		
			System.out.println(currentDate + " " + currentTime);
			List<Booking> bookings = bookingRepo.getCurrentBookingsByEmployee(employeeId, currentDate, currentTime);

			if(bookings.isEmpty()) {
				throw new EvChargingException("User with id " + employeeId + " has no current booking");
			}

			return bookings;
			
	}
	
	/* (non-Javadoc)
	 * @see com.capgemini.evCharging.service.EvChargingService#rescheduleBooking(java.lang.Integer, java.sql.Date, java.time.LocalTime, java.lang.Integer, java.lang.Integer)
	 */
	@Override
	public List<Booking> rescheduleBooking(Integer ticketNo, Date rescheduledBookedDate, LocalTime rescheduledBookingStartTiming, Integer machineId, Integer employeeId) throws EvChargingException {
		Booking booking = Utility.utilityObject.getBookingFromTicketNo(ticketNo,bookingRepo);

		booking.setStatus(BookingStatus.RESCHEDULED);

		bookMachine(rescheduledBookedDate, rescheduledBookingStartTiming, machineId, employeeId);
		bookingRepo.save(booking);
		
		return bookingRepo.findAll();
	}


	/* (non-Javadoc)
	 * @see com.capgemini.evCharging.service.EvChargingService#cancelBooking(java.lang.Integer)
	 */
	@Override
	public List<Booking> cancelBooking(Integer ticketNo) throws EvChargingException {

		Booking booking = Utility.utilityObject.getBookingFromTicketNo(ticketNo,bookingRepo);

		booking.setStatus(BookingStatus.CANCELLED);

		bookingRepo.save(booking);
		return bookingRepo.findAll();

	}
	
	
	/**
	 * @param selectedSlotDuration
	 * @param stationId
	 * @param selectedDate
	 * @return
	 */
	public List<Machine> getActiveMachinesOfDurationAndStation(SlotDuration selectedSlotDuration, Integer stationId,Date selectedDate) {

		return machineRepo.getActiveMachinesOfStationAndDuration(selectedSlotDuration, stationId, MachineStatus.ACTIVE, selectedDate);

	}

	/* (non-Javadoc)
	 * @see com.capgemini.evCharging.service.EvChargingService#getMachineBookingDetail(java.sql.Date, com.capgemini.evCharging.bean.enums.SlotDuration, java.lang.Integer)
	 */
	@Override
	public MachineDetails getMachineBookingDetail(Date selectedDate, SlotDuration selectedDuration, Integer stationId) throws EvChargingException {
		getMachinesWhichCanResume();
		List<Machine> machines =  getActiveMachinesOfDurationAndStation(selectedDuration,stationId,selectedDate);
		MachineDetails machineDetails = new MachineDetails(selectedDuration);
		machineDetails =  Utility.utilityObject.populateMachineDetails(machineDetails,machines);

		for (Machine machine : machines) {
			List<Booking> bookings = getBookingsOfMachine(machine.getMachineId(), selectedDate);

			for(Booking booking : bookings) {

			
				machineDetails = updateMachineBookingDetail(booking, machineDetails);

			}
		}

		return machineDetails;
	}
	

	/* (non-Javadoc)
	 * @see com.capgemini.evCharging.service.EvChargingService#addMachines(java.lang.Integer, java.util.List)
	 */
	@Override
	public List<Machine> addMachines(Integer stationId, List<Machine> machines) throws EvChargingException {

		Station station = Utility.utilityObject.getStationFromStationId(stationId, stationRepo);
		Date currentDate = new Date(System.currentTimeMillis());
		for (Machine machine : machines) {
			machine.setMachineStation(station);
			
			if(machine.getStartingDate().after(currentDate) && machine.getMachineStatus() == MachineStatus.ACTIVE) {
				throw new EvChargingException("Machine with machine " + machine.getMachineName() + " can't have an active state for later than " + currentDate + " date");
			}
			machine.setMachineStatus(MachineStatus.ACTIVE);
			machine =  machineRepo.save(machine);
			machine.setMachineName("M" +  machine.getMachineId());
			machineRepo.save(machine);
		}
		
		return machineRepo.findAll();

	}



	/* (non-Javadoc)
	 * @see com.capgemini.evCharging.service.EvChargingService#removeMachine(java.lang.Integer)
	 */
	@Override
	public List<Machine> removeMachine(Integer machineId) throws EvChargingException {
		
		if (!machineRepo.existsById(machineId)) {
			throw new EvChargingException("Machine with " + machineId + " doesn't exist");
		}
		Machine machine =  Utility.utilityObject.getMachineFromMachineId(machineId, machineRepo);
		machine.setMachineStatus(MachineStatus.REMOVED);
		machineRepo.save(machine);
 		return machineRepo.findAll();
	}

	/* (non-Javadoc)
	 * @see com.capgemini.evCharging.service.EvChargingService#haltMachine(java.lang.Integer, java.sql.Date)
	 */
	@Override
	public Machine haltMachine(Integer machineId, Date newStartDate) throws EvChargingException {
		if(!machineRepo.existsById(machineId)) {
			throw new EvChargingException("Machine with " + machineId + " doesn't exist");
		}
		Machine machine = Utility.utilityObject.getMachineFromMachineId(machineId, machineRepo);
		machine.setStartingDate(newStartDate);
		machine.setMachineStatus(MachineStatus.HALTED);
		machineRepo.save(machine);
		return machineRepo.findById(machineId).get();
	}
	
	/* (non-Javadoc)
	 * @see com.capgemini.evCharging.service.EvChargingService#haltMachine(java.lang.Integer, java.sql.Date, java.time.LocalTime, java.time.LocalTime)
	 */
	@Override
	public Machine haltMachine(Integer machineId, Date newStartDate, LocalTime newStartTime, LocalTime newEndTime)
			throws EvChargingException {
		if(!machineRepo.existsById(machineId)) {
			throw new EvChargingException("Machine with " + machineId + " doesn't exist");
		}
		Machine machine = Utility.utilityObject.getMachineFromMachineId(machineId, machineRepo);
		machine.setStartingDate(newStartDate);
		machine.setStartTime(newStartTime);
		machine.setEndTime(newEndTime);
		machine.setMachineStatus(MachineStatus.HALTED);
		machineRepo.save(machine);
		return machineRepo.findById(machineId).get();
	}


	/* (non-Javadoc)
	 * @see com.capgemini.evCharging.service.EvChargingService#resumeMachine(java.lang.Integer)
	 */
	@Override
	public Machine resumeMachine(Integer machineId) throws EvChargingException {
		Machine machine = Utility.utilityObject.getMachineFromMachineId(machineId, machineRepo);
		if (machine.getMachineStatus() == MachineStatus.ACTIVE) {
			throw new EvChargingException("Machine with " + machineId  + " is already in active state");
		}
		machine.setMachineStatus(MachineStatus.ACTIVE);
		machine.setStartingDate(new Date(System.currentTimeMillis()));
		machineRepo.save(machine);
		return machineRepo.findById(machineId).get();
	}

	/* (non-Javadoc)
	 * @see com.capgemini.evCharging.service.EvChargingService#modifyMachine(com.capgemini.evCharging.bean.Machine)
	 */
	@Override
	public Machine modifyMachine(Machine modifiedMachine) throws EvChargingException {
		
		if(!machineRepo.existsById(modifiedMachine.getMachineId())) {
			throw new EvChargingException("Machine with " + modifiedMachine.getMachineId() + " doesn't exist");
		}
		Machine toBeModifyMachine = Utility.utilityObject.getMachineFromMachineId(modifiedMachine.getMachineId(), machineRepo);
		addMachines(toBeModifyMachine.getMachineStation().getStationId(),new ArrayList<Machine>(Arrays.asList(modifiedMachine)));
		return Utility.utilityObject.getMachineFromMachineId(modifiedMachine.getMachineId(), machineRepo);
	}

	/* (non-Javadoc)
	 * @see com.capgemini.evCharging.service.EvChargingService#generateBookingsReport(java.lang.Integer, java.sql.Date, java.sql.Date)
	 */
	@Override
	public List<ReportFormat> generateBookingsReport(Integer stationId,Date fromDate, Date toDate) {
		List<Map<String,Object>> reportList =  bookingRepo.generateBookingsReportByJoin(fromDate, toDate, stationId);
		List<ReportFormat> reports = new ArrayList<ReportFormat>();
		for(Map<String,Object> _report : reportList) {
			ReportFormat report = new ReportFormat();
			report.setBookingsCount( (Long)_report.get("bookedMachineCount") );
			report.setBookedMachine( (Machine)_report.get("bookedMachine"));
			reports.add(report);
		}
		return reports;
	}

	/* (non-Javadoc)
	 * @see com.capgemini.evCharging.service.EvChargingService#generateMachineBookingsReport(java.lang.Integer, java.sql.Date, java.sql.Date)
	 */
	@Override
	public List<Booking> generateMachineBookingsReport(Integer machineId,Date fromDate, Date toDate) {
		List<Booking> bookings = bookingRepo.getBookingsOfMachine(machineId, fromDate, toDate);
		return bookings;
	}

	/* (non-Javadoc)
	 * @see com.capgemini.evCharging.service.EvChargingService#addStation(java.lang.String, java.lang.String)
	 */
	@Override
	public List<Station> addStation(String city, String campusLocation) {
		Station newStation = new Station();
		newStation.setCity(city);
		newStation.setCampusLocation(campusLocation);
		stationRepo.save(newStation);
		
		return stationRepo.findAll();
	}

	
	
	
	


}











