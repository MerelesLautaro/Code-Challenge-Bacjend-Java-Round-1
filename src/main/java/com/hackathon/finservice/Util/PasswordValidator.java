package com.hackathon.finservice.Util;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordValidator implements ConstraintValidator<Password, String> {

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {

        String message;
        context.disableDefaultConstraintViolation();

        boolean notContainsUppercase = password == null || !password.matches("^.*[A-Z]+.*$");
        if (notContainsUppercase) {
            message = "Password must contain at least one uppercase letter";
            context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
            return false;
        }

        boolean notContainsDigitOrSpecialCharacter = !password.matches("^.*\\d+.*$");
        if (notContainsDigitOrSpecialCharacter) {
            message = "Password must contain at least one digit and one special character";
            context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
            return false;
        }

        boolean notContainsSpecialCharacter = !password.matches("^(?=.*[\\W_]).*$");
        if (notContainsSpecialCharacter) {
            message = "Password must contain at least one special character";
            context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
            return false;
        }

        boolean containsWhitespace = !password.matches("^\\S*$");
        if (containsWhitespace) {
            message = "Password cannot contain whitespace";
            context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
            return false;
        }

        if (password.length() >= 128) {
            message = "Password must be less than 128 characters long";
            context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
            return false;
        }

        if (password.length() < 8) {
            message = "Password must be at least 8 characters long";
            context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
            return false;
        }

        return true;
    }
}
