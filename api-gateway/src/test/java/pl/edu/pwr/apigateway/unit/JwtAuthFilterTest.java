package pl.edu.pwr.apigateway.unit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.http.server.reactive.MockServerHttpResponse;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.ServerWebExchange;
import pl.edu.pwr.apigateway.jwt.JwtAuthFilter;
import pl.edu.pwr.apigateway.jwt.JwtService;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class JwtAuthFilterTest {
    @Mock
    private JwtService jwtService;

    @InjectMocks
    private JwtAuthFilter jwtAuthFilter;

    private String extractResponseBody(ServerWebExchange exchange) {
        MockServerHttpResponse response = (MockServerHttpResponse) exchange.getResponse();
        return response.getBodyAsString().block();
    }

    @Test
    void should_returnUnauthorized_whenAuthorizationHeaderMissing() {
        // given
        MockServerHttpRequest request = MockServerHttpRequest.get("/test").build();
        ServerWebExchange exchange = MockServerWebExchange.from(request);
        GatewayFilterChain chain = mock(GatewayFilterChain.class);

        // when
        Mono<Void> result = jwtAuthFilter.filter(exchange, chain);

        // then
        StepVerifier.create(result)
                .expectComplete()
                .verify();

        assertEquals(HttpStatus.UNAUTHORIZED, exchange.getResponse().getStatusCode());
        assertTrue(extractResponseBody(exchange).contains("Authorization header is missing"));
    }

    @Test
    void should_returnUnauthorized_whenAuthorizationHeaderNotBearer() {
        // given
        MockServerHttpRequest request = MockServerHttpRequest.get("/test")
                .header(HttpHeaders.AUTHORIZATION, "Token xyz.abc.123")
                .build();
        ServerWebExchange exchange = MockServerWebExchange.from(request);
        GatewayFilterChain chain = mock(GatewayFilterChain.class);

        // when
        Mono<Void> result = jwtAuthFilter.filter(exchange, chain);

        // then
        StepVerifier.create(result).expectComplete().verify();

        assertEquals(HttpStatus.UNAUTHORIZED, exchange.getResponse().getStatusCode());
        assertTrue(extractResponseBody(exchange).contains("Authorization header must be Bearer token"));
    }

    @Test
    void should_returnUnauthorized_whenTokenIsInvalidOrExpired() {
        // given
        String token = "invalid.token.123";
        MockServerHttpRequest request = MockServerHttpRequest.get("/test")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .build();
        ServerWebExchange exchange = MockServerWebExchange.from(request);
        GatewayFilterChain chain = mock(GatewayFilterChain.class);

        when(jwtService.isTokenValid(token)).thenReturn(false);

        // when
        Mono<Void> result = jwtAuthFilter.filter(exchange, chain);

        // then
        StepVerifier.create(result).expectComplete().verify();

        assertEquals(HttpStatus.UNAUTHORIZED, exchange.getResponse().getStatusCode());
        assertTrue(extractResponseBody(exchange).contains("Invalid or expired token"));
    }

    @Test
    void should_continueChainAndSetUserHeaders_whenTokenIsValid() {
        // given
        String token = "valid.jwt.token";
        String username = "john";
        String role = "USER";

        MockServerHttpRequest request = MockServerHttpRequest.get("/test")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        GatewayFilterChain chain = mock(GatewayFilterChain.class);
        when(chain.filter(any())).thenReturn(Mono.empty());

        when(jwtService.isTokenValid(token)).thenReturn(true);
        when(jwtService.getUsername(token)).thenReturn(username);
        when(jwtService.getRole(token)).thenReturn(role);

        // when
        Mono<Void> result = jwtAuthFilter.filter(exchange, chain);

        // then
        StepVerifier.create(result).verifyComplete();

        ArgumentCaptor<ServerWebExchange> captor = ArgumentCaptor.forClass(ServerWebExchange.class);
        verify(chain).filter(captor.capture());

        ServerHttpRequest mutatedRequest = captor.getValue().getRequest();
        assertEquals(username, mutatedRequest.getHeaders().getFirst("X-User-Id"));
        assertEquals(role, mutatedRequest.getHeaders().getFirst("X-User-Roles"));
    }
}
