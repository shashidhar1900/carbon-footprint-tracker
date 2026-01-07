package com.shashi.analyticsservice.repository;

import com.shashi.analyticsservice.model.UserMonthlyTransportEmission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserMonthlyTransportEmissionRepository extends JpaRepository<UserMonthlyTransportEmission, String> {

}
