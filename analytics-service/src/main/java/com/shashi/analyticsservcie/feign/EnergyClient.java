package com.shashi.analyticsservcie.feign;

import com.shashi.analyticsservcie.config.FeignClientConfig;
import com.shashi.analyticsservcie.dto.EnergyResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "energy-service", configuration = FeignClientConfig.class)
public interface EnergyClient {
    @GetMapping("api/energy/history/{username}/{year}/{month}")
    EnergyResponse getMonthlyEnergy(@PathVariable String username,
                                          @PathVariable int year,
                                          @PathVariable int month);
}
