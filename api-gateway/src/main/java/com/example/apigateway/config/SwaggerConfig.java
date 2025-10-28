//package com.example.apigateway.config;
//
//import io.swagger.v3.oas.models.OpenAPI;
//import io.swagger.v3.oas.models.info.Info;
//import org.springdoc.core.models.GroupedOpenApi;
//import org.springframework.cloud.gateway.route.RouteDefinitionLocator;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Lazy;
//
//import java.util.ArrayList;
//import java.util.List;
//
//@Configuration
//public class SwaggerConfig {
//
//    private final RouteDefinitionLocator locator;
//
//    public SwaggerConfig(RouteDefinitionLocator locator) {
//        this.locator = locator;
//    }
//
//    // Bean to define the metadata for the API Gateway's documentation
//    @Bean
//    public OpenAPI customOpenAPI() {
//        return new OpenAPI()
//                .info(new Info().title("API Gateway Documentation").version("1.0"));
//    }
//
//    // Bean to aggregate the documentation from all microservices
//    @Bean
//    @Lazy(false) // Ensures this runs early
//    public List<GroupedOpenApi> apis() {
//        List<GroupedOpenApi> groups = new ArrayList<>();
//
//        // This iterates through all the routes defined in your application.yml/properties
//        // and creates a Swagger group for each microservice.
//        locator.getRouteDefinitions().subscribe(routeDefinition -> {
//            String routeId = routeDefinition.getId();
//
//            // Exclude the documentation route itself or any unwanted routes
//            if (!routeId.equals("documentation")) {
//                groups.add(GroupedOpenApi.builder()
//                        .pathsToMatch("/" + routeId + "/**") // Match paths routed to this microservice
//                        .group(routeId) // Use the route ID (e.g., 'user-service') as the group name
//                        .build());
//            }
//        });
//        return groups;
//    }
//}
