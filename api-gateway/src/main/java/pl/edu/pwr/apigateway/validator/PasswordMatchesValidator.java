package pl.edu.pwr.apigateway.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import pl.edu.pwr.apigateway.dto.RegisterRequest;

public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, RegisterRequest> {

    @Override
    public boolean isValid(RegisterRequest request, ConstraintValidatorContext context) {
        return request.password().equals(request.confirmPassword());
    }
}
