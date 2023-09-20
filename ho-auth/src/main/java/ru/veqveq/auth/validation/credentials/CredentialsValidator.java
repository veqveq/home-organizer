package ru.veqveq.auth.validation.credentials;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.veqveq.auth.dto.LoginRequest;
import ru.veqveq.auth.service.UserService;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

@Slf4j
@Service
@RequiredArgsConstructor
public class CredentialsValidator implements ConstraintValidator<CredentialsValid, LoginRequest> {
    private final UserService userService;

    @Override
    public boolean isValid(LoginRequest value, ConstraintValidatorContext context) {
        context.disableDefaultConstraintViolation();
        boolean isValid = true;
        if (userService.checkUsernameIsExist(value.getUsername())) {
            addConstraintViolation(context, String.format("Пользователь с именем: [%s] существует", value.getUsername()));
            isValid = false;
        }
        if (userService.checkEmailIsExist(value.getEmail())) {
            addConstraintViolation(context, String.format("Пользователь с email: [%s] существует", value.getEmail()));
            isValid = false;
        }
        return isValid;
    }

    private void addConstraintViolation(ConstraintValidatorContext context, String message) {
        context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
    }
}
