package com.shashi.analyticsservice.repository;

import com.shashi.analyticsservice.model.UserMonthlyEnergyEmission;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserMonthlyEnergyEmissionRepository extends JpaRepository<UserMonthlyEnergyEmission, String> {
}
