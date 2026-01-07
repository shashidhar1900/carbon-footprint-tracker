package com.shashi.analyticsservice.feign;

import com.shashi.analyticsservice.config.FeignClientConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(name = "auth-service", configuration = FeignClientConfig.class)
public interface AuthClient {

    @GetMapping("api/auth/users")
    List<String> getAllUsernames();

    @GetMapping("api/auth/debug")
    String debug();

}
