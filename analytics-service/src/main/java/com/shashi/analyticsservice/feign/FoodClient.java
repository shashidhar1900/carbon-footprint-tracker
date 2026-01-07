package com.shashi.analyticsservice.feign;

import com.shashi.analyticsservice.config.FeignClientConfig;
import com.shashi.analyticsservice.dto.FoodResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@FeignClient(name = "food-service", configuration = FeignClientConfig.class)
public interface FoodClient {
    @GetMapping("api/food/history/{username}/{year}/{month}")
    FoodResponse getMonthlyFood(@PathVariable String username,
                                      @PathVariable int year,
                                      @PathVariable int month);
}
