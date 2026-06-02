package com.project.hotelmanagement.validator;

import com.project.hotelmanagement.enums.PaymentMethod;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;

public class PaymentSubsetValidator implements ConstraintValidator<PaymentMethodSubset, PaymentMethod> {
    private PaymentMethod [] paymentMethods;
    @Override
    public void initialize(PaymentMethodSubset constraint) {
        this.paymentMethods = constraint.anyOf();
    }

    @Override
    public boolean isValid(PaymentMethod value, ConstraintValidatorContext context) {
        return value == null || Arrays.asList(paymentMethods).contains(value);
    }
}
