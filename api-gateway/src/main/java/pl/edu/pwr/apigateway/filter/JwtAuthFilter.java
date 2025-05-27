package pl.edu.pwr.apigateway.filter;

import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import pl.edu.pwr.apigateway.auth.AuthRepository;
import pl.edu.pwr.apigateway.jwt.JwtService;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter implements GatewayFilter, Ordered {

    private final JwtService jwtService;
    private final AuthRepository authRepository;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        if(!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
            return FilterUtils.onError(exchange, "Authorization header is missing", HttpStatus.UNAUTHORIZED);
        }

        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        String token;
        if(authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
        } else {
            return FilterUtils.onError(exchange, "Authorization header must be Bearer token", HttpStatus.UNAUTHORIZED);
        }

        if(!jwtService.isTokenValid(token)) {
            return FilterUtils.onError(exchange, "Invalid or expired token", HttpStatus.UNAUTHORIZED);
        }

        String username = jwtService.getUsername(token);
        return authRepository.findByUsername(username)
                .flatMap(user -> {
                    String userId = user.getId().toString();

                    ServerHttpRequest modifiedRequest = exchange.getRequest().mutate()
                            .header("X-User-Username", username)
                            .header("X-User-Id", userId)
                            .header("X-User-Roles", jwtService.getRole(token))
                            .build();
                    return chain.filter(exchange.mutate().request(modifiedRequest).build());
                });
    }

    @Override
    public int getOrder() {
        return -1;
    }
}
