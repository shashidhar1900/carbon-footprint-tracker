package com.shashi.analyticsservice.feign;

import com.shashi.analyticsservice.config.FeignClientConfig;
import com.shashi.analyticsservice.dto.TransportResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "transport-service", configuration = FeignClientConfig.class)
public interface TransportClient {
    @GetMapping("api/transport/history/{username}/{year}/{month}")
    TransportResponse getMonthlyTransport(@PathVariable String username,
                                                @PathVariable int year,
                                                @PathVariable int month);
}
