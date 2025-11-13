package com.example.client.validation;

import java.util.Optional;

public interface ValidationStrategy {

    Optional<String> validate(String value);
}
