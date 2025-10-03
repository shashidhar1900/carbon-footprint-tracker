package com.shashi.analyticsservcie.repository;

import com.shashi.analyticsservcie.model.UserMonthlyTotalEmission;
import org.springframework.data.jpa.repository.JpaRepository;


public interface UserMonthlyEmissionRepository extends JpaRepository<UserMonthlyTotalEmission, String> {

}
