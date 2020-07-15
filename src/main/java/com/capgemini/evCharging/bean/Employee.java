package com.capgemini.evCharging.bean;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import com.capgemini.evCharging.bean.enums.MachineType;


//
//EvCharging Application
//
//Created by The Local host on June 28 2020.
//Copyright © 2020 Local host. All rights reserved.
//


//This entity class stores the Employee information
@Entity
public class Employee {
	
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer employeeId;


	@Column(unique = true, nullable = false)
	private String mailId;

	@Column(nullable = false)
	private String empName;
	
	@Column(nullable = false)
	private String phoneNo;
	
	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private MachineType employeeMachineType;
	
	@ManyToOne
	private Station employeeStation;

	public Integer getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(Integer employeeId) {
		this.employeeId = employeeId;
	}

	public String getMailId() {
		return mailId;
	}

	public void setMailId(String mailId) {
		this.mailId = mailId;
	}

	public String getEmpName() {
		return empName;
	}

	public void setEmpName(String empName) {
		this.empName = empName;
	}

	public String getPhoneNo() {
		return phoneNo;
	}

	public void setPhoneNo(String phoneNo) {
		this.phoneNo = phoneNo;
	}

	public MachineType getEmployeeMachineType() {
		return employeeMachineType;
	}

	public void setEmployeeMachineType(MachineType employeeMachineType) {
		this.employeeMachineType = employeeMachineType;
	}

	public Station getEmployeeStation() {
		return employeeStation;
	}

	public void setEmployeeStation(Station employeeStation) {
		this.employeeStation = employeeStation;
	}

	@Override
	public String toString() {
		return "Employee [employeeId=" + employeeId + ", mailId=" + mailId + ", empName=" + empName + ", phoneNo="
				+ phoneNo + ", employeeMachineType=" + employeeMachineType + ", employeeStation=" + employeeStation
				+ "]";
	}

}
