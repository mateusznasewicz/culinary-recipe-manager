package pl.edu.pwr.apigateway.dto;

import jakarta.validation.constraints.NotBlank;
import pl.edu.pwr.apigateway.validator.PasswordMatches;

@PasswordMatches
public record RegisterRequest(
        @NotBlank(message = "Username must not be blank") String username,
        @NotBlank(message = "Password must not be blank") String password,
        @NotBlank(message = "Confirm password must not be blank") String confirmPassword) {
}
