package com.example.client.validation;

import java.util.Optional;
import java.util.regex.Pattern;

public class EmailValidationStrategy implements ValidationStrategy {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    @Override
    public Optional<String> validate(String value) {
        if (value == null || value.isBlank()) {
            return Optional.of("L'email è obbligatoria");
        }
        if (!EMAIL_PATTERN.matcher(value).matches()) {
            return Optional.of("Formato email non valido");
        }
        return Optional.empty();
    }
}
