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

import com.capgemini.evCharging.bean.Station;

//This is a repository that directly handles the database layer of Station entity.
@Repository
public interface StationDao extends JpaRepository<Station, Integer>{

	//These queries are in the Hibernate query language (HQL)
	@Query("select S from Station S where S.city=:city and S.campusLocation=:campusLocation")
	public Optional<Station> checkIfStationExists(@Param("city") String city, @Param("campusLocation") String campusLocation);
	
}
