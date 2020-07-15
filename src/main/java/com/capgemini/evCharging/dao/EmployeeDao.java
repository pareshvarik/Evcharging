package com.capgemini.evCharging.dao;

//
//EvCharging Application
//
//Created by The Local host on June 28 2020.
//Copyright © 2020 Local host. All rights reserved.
//

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.capgemini.evCharging.bean.Employee;



//This is a repository that directly handles the database layer of Employee entity.
@Repository
public interface EmployeeDao extends JpaRepository<Employee,Integer> {

	//These queries are in the Hibernate query language (HQL)
	@Query("select E from Employee E where E.mailId=:mailId")
	public Optional<Employee> findByMailId(@Param("mailId") String mailId);
}
