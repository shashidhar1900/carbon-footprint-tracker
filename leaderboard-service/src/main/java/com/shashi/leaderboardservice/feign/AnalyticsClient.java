package com.shashi.leaderboardservice.feign;


import com.shashi.leaderboardservice.dto.AnalyticsResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(name = "analytics-service")
public interface AnalyticsClient {

    @GetMapping("/api/analytics/monthly")
    List<AnalyticsResponse> getAnalyticsMonthly();

}
