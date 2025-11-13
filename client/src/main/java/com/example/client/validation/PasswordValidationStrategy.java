package com.example.client.validation;

import java.util.Optional;

public class PasswordValidationStrategy implements ValidationStrategy {

    @Override
    public Optional<String> validate(String value) {
        if (value == null || value.isBlank()) {
            return Optional.of("La password è obbligatoria");
        }
        if (value.length() < 8) {
            return Optional.of("La password deve contenere almeno 8 caratteri");
        }
        if (!value.matches(".*[A-Z].*")) {
            return Optional.of("La password deve contenere una lettera maiuscola");
        }
        if (!value.matches(".*[a-z].*")) {
            return Optional.of("La password deve contenere una lettera minuscola");
        }
        if (!value.matches(".*\\d.*")) {
            return Optional.of("La password deve contenere un numero");
        }
        return Optional.empty();
    }
}
