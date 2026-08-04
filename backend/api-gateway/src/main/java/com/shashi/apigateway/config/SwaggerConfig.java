package com.shashi.apigateway.config;

import org.springdoc.core.properties.AbstractSwaggerUiConfigProperties;
import org.springdoc.core.properties.SwaggerUiConfigProperties;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionLocator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Configuration
public class SwaggerConfig {

    @Bean
    @Lazy(false)
    public Set<AbstractSwaggerUiConfigProperties.SwaggerUrl> swaggerUrls(
            RouteDefinitionLocator locator) {

        Set<AbstractSwaggerUiConfigProperties.SwaggerUrl> urls = new HashSet<>();

        List<RouteDefinition> definitions = locator.getRouteDefinitions().collectList().block();

        definitions.stream()
                .filter(routeDefinition -> routeDefinition.getId().matches(".*SERVICE"))
                .forEach(routeDefinition -> {
                    String name = routeDefinition.getId();
                    String url = "/" + name.toLowerCase() + "/v3/api-docs";
                    urls.add(new AbstractSwaggerUiConfigProperties.SwaggerUrl(name, url, null));
                });

        return urls;
    }
}
