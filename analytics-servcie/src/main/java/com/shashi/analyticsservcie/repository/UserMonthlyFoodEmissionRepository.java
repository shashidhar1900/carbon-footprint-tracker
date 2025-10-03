package com.shashi.analyticsservcie.repository;

import com.shashi.analyticsservcie.model.UserMonthlyFoodEmission;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserMonthlyFoodEmissionRepository extends JpaRepository<UserMonthlyFoodEmission, String> {
}
