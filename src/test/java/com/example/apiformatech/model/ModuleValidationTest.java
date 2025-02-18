package com.example.apiformatech.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ModuleValidationTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testValidModule() {
        Module module = new Module();
        module.setName("Java");
        module.setDescription("Valid description");

        Set<ConstraintViolation<Module>> violations = validator.validate(module);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testInvalidModule() {
        Module module = new Module();
        module.setName("");

        Set<ConstraintViolation<Module>> violations = validator.validate(module);
        assertFalse(violations.isEmpty());
    }
}
