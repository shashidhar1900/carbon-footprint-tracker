package com.shashi.energyservice;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@OpenAPIDefinition(
		servers = {
				@Server(
						url = "http://localhost:8961/energy-service"
				)
		}
)
@SpringBootApplication
public class EneryServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(EneryServiceApplication.class, args);
	}

}
