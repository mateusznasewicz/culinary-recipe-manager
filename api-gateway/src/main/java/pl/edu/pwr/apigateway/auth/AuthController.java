package pl.edu.pwr.apigateway.auth;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.edu.pwr.apigateway.dto.LoginRequest;
import pl.edu.pwr.apigateway.dto.LoginResponse;
import pl.edu.pwr.apigateway.dto.RegisterRequest;
import pl.edu.pwr.apigateway.jwt.JwtService;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtService jwtService;

    @PostMapping("/login")
    public Mono<ResponseEntity<LoginResponse>> login(@RequestBody @Valid LoginRequest request){
        return authService.login(request.username(), request.password())
                .then(Mono.fromCallable(() -> {
                    String token = jwtService.generateToken(request.username());
                    LoginResponse response = new LoginResponse(token, "Bearer", "Login successful");
                    return ResponseEntity.ok(response);
        }));
    }

    @PostMapping("/register")
    public Mono<ResponseEntity<String>> register(@RequestBody @Valid RegisterRequest request){
        return authService.register(request.username(), request.password())
                .thenReturn(ResponseEntity.ok("Registration successful"));
    }
}
