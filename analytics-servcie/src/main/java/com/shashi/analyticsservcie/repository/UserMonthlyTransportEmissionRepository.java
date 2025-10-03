package com.shashi.analyticsservcie.repository;

import com.shashi.analyticsservcie.model.UserMonthlyTransportEmission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserMonthlyTransportEmissionRepository extends JpaRepository<UserMonthlyTransportEmission, String> {

}
