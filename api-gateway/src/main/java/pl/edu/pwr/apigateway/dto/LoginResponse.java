package pl.edu.pwr.apigateway.dto;

public record LoginResponse(String token, String type, String message) {
}
