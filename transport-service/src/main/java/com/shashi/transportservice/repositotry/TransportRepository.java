package com.shashi.transportservice.repositotry;

import com.shashi.transportservice.model.TransportUsage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransportRepository extends JpaRepository<TransportUsage, Integer> {


    List<TransportUsage> findByUsername(String username);

    @Query("SELECT DISTINCT t.username FROM TransportUsage t")
    List<String> findDistinctUsernames();

}
