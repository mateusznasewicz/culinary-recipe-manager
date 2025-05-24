package pl.edu.pwr.apigateway.config;

import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.edu.pwr.apigateway.jwt.JwtAuthFilter;

@Configuration
@RequiredArgsConstructor
public class RouteConfiguration {

    private final JwtAuthFilter jwtAuthFilter;

    @Bean
    public RouteLocator routeLocator(RouteLocatorBuilder builder){
        return builder.routes()
                .route("read-service", r -> r.path("/api/**")
                        .filters(f -> f.filter(jwtAuthFilter).stripPrefix(0))
                        .uri("http://localhost:8081"))

                .route("write-service", r -> r.path("/api/**")
                        .filters(f -> f.filter(jwtAuthFilter).stripPrefix(0))
                        .uri("http://localhost:8082"))
                .build();
    }
}
