package com.project.hotelmanagement.dto.request;

import com.project.hotelmanagement.enums.PaymentMethod;
import com.project.hotelmanagement.enums.PaymentStatus;
import com.project.hotelmanagement.validator.PaymentMethodSubset;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import static com.project.hotelmanagement.enums.PaymentMethod.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PaymentRequest {
    @NotBlank(message = "PAYMENT_CODE_INVALID")
    private String paymentCode;

    @Positive
    private Double amount;

    @PaymentMethodSubset(anyOf = {CASH,CREDIT_CARD,MOMO,VNPAY})
    private PaymentMethod paymentMethod;

    @NotNull(message = "PAYMENT_STATUS_INVALID")
    private PaymentStatus status;

    private Date paymentDate;
    private String note;

}
