package com.shashi.foodservice.repository;


import com.shashi.foodservice.model.FoodUsage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FoodRepository extends JpaRepository<FoodUsage, Integer> {
    List<FoodUsage> findByUsername(String username);

    FoodUsage findByUsernameAndDateAndType(String username, String string, String type);
}
