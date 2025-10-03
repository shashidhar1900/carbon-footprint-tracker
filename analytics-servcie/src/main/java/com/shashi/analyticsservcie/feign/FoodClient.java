package com.shashi.analyticsservcie.feign;

import com.shashi.analyticsservcie.config.FeignClientConfig;
import com.shashi.analyticsservcie.dto.FoodResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;


@FeignClient(name = "food-service", configuration = FeignClientConfig.class)
public interface FoodClient {
    @GetMapping("api/food/history/{username}/{year}/{month}")
    FoodResponse getMonthlyFood(@PathVariable String username,
                                      @PathVariable int year,
                                      @PathVariable int month);
}
