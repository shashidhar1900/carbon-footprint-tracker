package com.shashi.analyticsservcie.feign;

import com.shashi.analyticsservcie.config.FeignClientConfig;
import com.shashi.analyticsservcie.dto.TransportResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

@FeignClient(name = "transport-service", configuration = FeignClientConfig.class)
public interface TransportClient {
    @GetMapping("api/transport/history/{username}/{year}/{month}")
    TransportResponse getMonthlyTransport(@PathVariable String username,
                                                @PathVariable int year,
                                                @PathVariable int month);
}
