package com.shashi.analyticsservice.repository;

import com.shashi.analyticsservice.model.UserMonthlyTotalEmission;
import org.springframework.data.jpa.repository.JpaRepository;


public interface UserMonthlyEmissionRepository extends JpaRepository<UserMonthlyTotalEmission, String> {

}
