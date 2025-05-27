package pl.edu.pwr.apigateway.config;

import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import pl.edu.pwr.apigateway.filter.AdminRoleFilter;
import pl.edu.pwr.apigateway.filter.JwtAuthFilter;

@Configuration
@RequiredArgsConstructor
public class RouteConfiguration {

    private final JwtAuthFilter jwtAuthFilter;
    private final AdminRoleFilter adminRoleFilter;

    @Bean
    public RouteLocator routeLocator(RouteLocatorBuilder builder){
        return builder.routes()
                .route("read-service", r -> r.path("/api/**")
                        .and()
                        .method(HttpMethod.GET)
                        .filters(f -> f.filter(jwtAuthFilter).stripPrefix(0))
                        .uri("http://localhost:8081"))

                .route("write-service-admin", r -> r.path("/api/tag/**", "/api/unit/**", "/api/ingredient/**")
                        .and()
                        .method(HttpMethod.POST, HttpMethod.PUT, HttpMethod.DELETE)
                        .filters(f -> f
                                .filter(jwtAuthFilter)
                                .filter(adminRoleFilter)
                                .stripPrefix(0))
                        .uri("http://localhost:8082"))

                .route("write-service", r -> r.path("/api/**")
                        .and()
                        .method(HttpMethod.POST, HttpMethod.PUT, HttpMethod.DELETE)
                        .filters(f -> f.filter(jwtAuthFilter).stripPrefix(0))
                        .uri("http://localhost:8082"))
                .build();
    }
}
