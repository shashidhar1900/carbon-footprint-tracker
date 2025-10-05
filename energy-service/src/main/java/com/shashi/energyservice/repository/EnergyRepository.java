package com.shashi.energyservice.repository;

import com.shashi.energyservice.model.EnergyUsage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EnergyRepository extends JpaRepository<EnergyUsage,Integer> {

    List<EnergyUsage> findByUsername(String username);

    EnergyUsage findByUsernameAndDate(String username, String string);
}
