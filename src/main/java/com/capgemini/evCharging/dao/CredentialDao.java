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

import com.capgemini.evCharging.bean.Credential;


//This is a repository that directly handles the database layer of Credential entity.
@Repository
public interface CredentialDao extends JpaRepository<Credential, Integer>{
	
	//These queries are in the Hibernate query language (HQL)
	@Query("select C from Credential C where C.mailId=:mailId")
	public Optional<Credential> findByMailId(@Param("mailId") String mailId);

}
