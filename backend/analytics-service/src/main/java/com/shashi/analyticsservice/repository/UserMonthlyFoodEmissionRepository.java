package com.shashi.analyticsservice.repository;

import com.shashi.analyticsservice.model.UserMonthlyFoodEmission;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserMonthlyFoodEmissionRepository extends JpaRepository<UserMonthlyFoodEmission, String> {
}
