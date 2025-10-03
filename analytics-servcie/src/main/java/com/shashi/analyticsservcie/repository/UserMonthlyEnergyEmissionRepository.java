package com.shashi.analyticsservcie.repository;

import com.shashi.analyticsservcie.model.UserMonthlyEnergyEmission;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserMonthlyEnergyEmissionRepository extends JpaRepository<UserMonthlyEnergyEmission, String> {
}
