package com.capgemini.evCharging.dao;


//
//EvCharging Application
//
//Created by The Local host on June 28 2020.
//Copyright © 2020 Local host. All rights reserved.
//

import java.sql.Date;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.capgemini.evCharging.bean.Booking;
import com.capgemini.evCharging.bean.Machine;
import com.capgemini.evCharging.bean.enums.BookingStatus;
import com.capgemini.evCharging.bean.enums.MachineType;

// This is a repository that directly handles the database layer of Booking entity.
@Repository
public interface BookingDao extends JpaRepository<Booking, Integer>{
	
	//These queries are in the Hibernate query language (HQL)
	
	@Query("select B from Booking B where B.bookingByEmployee.employeeId=:employeeId")
	public List<Booking> getAllBookingsByEmployee(@Param("employeeId") Integer employeeId);
	
	@Query("select B from Booking B where B.bookingByEmployee.employeeId=:employeeId and (B.bookedDate>:currentDate or (B.bookedDate=:currentDate and B.bookingStartTime>=:currentTime))")
	public List<Booking> getCurrentBookingsByEmployee(@Param("employeeId") Integer employeeId,@Param("currentDate") Date currentDate,@Param("currentTime") LocalTime currentTime);
	
	
	@Query("select B from Booking B where B.bookedMachine.machineId=:machineId and B.bookedDate=:selectedDate and B.status=:status")
	public List<Booking> getBookingsOfMachine(@Param("machineId") Integer machineId, @Param("selectedDate")Date selectedDate,@Param("status") BookingStatus status);
	
	@Query("select count(*) from Booking B where B.bookedMachine.machineStation.stationId=:stationId and B.bookedDate=:selectedDate and B.bookedMachine.machineType=:selectedMachineType and B.bookingStartTime>=:currentTime")
	public Integer getBookingsAtStationOnDateWithType(@Param("stationId")Integer stationId,@Param("selectedDate") Date selectedDate,@Param("selectedMachineType")MachineType selectedMachineType, @Param("currentTime") LocalTime currentTime);

	@Query("select B from Booking B where B.bookedMachine.machineId=:machineId and B.bookedDate=:bookedDate and B.bookingStartTime=:bookingStartTime")
	public Optional<Booking> checkIfMachineIsNotBooked(@Param("machineId") Integer machineId, @Param("bookedDate") Date bookedDate, @Param("bookingStartTime") LocalTime bookingStartTime);
	
	
	
	@Query("select new map(count(B.bookedMachine) as bookedMachineCount, B.bookedMachine as bookedMachine) from Booking B where B.bookedDate>=:fromDate and B.bookedDate<=:toDate and B.bookedMachine.machineStation.stationId=:selectedStationId group by B.bookedMachine.machineId")
	public List<Map<String, Object>> generateBookingsReportByJoin(@Param("fromDate") Date fromDate, @Param("toDate") Date toDate, @Param("selectedStationId") Integer selectedStationId);


	@Query("select B from Booking B where B.bookedMachine.machineId=:selectedMachineId and B.bookedDate>=:fromDate and B.bookedDate<=:toDate")
	public List<Booking> getBookingsOfMachine(@Param("selectedMachineId") Integer selectedMachineId, @Param("fromDate") Date fromDate, @Param("toDate") Date toDate);
}
