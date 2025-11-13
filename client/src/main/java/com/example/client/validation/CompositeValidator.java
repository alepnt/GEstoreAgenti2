package com.example.client.validation;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CompositeValidator {

    private final List<ValidationStrategy> strategies = new ArrayList<>();

    public CompositeValidator addStrategy(ValidationStrategy strategy) {
        strategies.add(strategy);
        return this;
    }

    public Optional<String> validate(String value) {
        for (ValidationStrategy strategy : strategies) {
            Optional<String> result = strategy.validate(value);
            if (result.isPresent()) {
                return result;
            }
        }
        return Optional.empty();
    }
}
