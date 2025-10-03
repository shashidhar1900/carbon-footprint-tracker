package com.shashi.analyticsservcie.feign;

import com.shashi.analyticsservcie.config.FeignClientConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

@FeignClient(name = "auth-service", configuration = FeignClientConfig.class)
public interface AuthClient {

    @GetMapping("api/auth/users")
    List<String> getAllUsernames();

    @GetMapping("api/auth/debug")
    String debug();

}
