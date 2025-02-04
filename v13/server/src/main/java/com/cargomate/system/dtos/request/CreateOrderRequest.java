package com.cargomate.system.dtos.request;

import com.cargomate.system.enums.PaymentMethod;
import com.cargomate.system.enums.PaymentStatus;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Getter
@Setter
public class CreateOrderRequest {
    @NotBlank(message = "Shipment address cannot be blank")
    private String shipmentAddress;

    @Future(message = "Delivery date must be in the future")
    @NotNull(message = "Delivery date is required")
    private LocalDateTime deliveryDate;

    @NotNull(message = "Payment method cannot be null")
    private PaymentMethod paymentMethod;

    @NotNull(message = "Payment status cannot be null")
    private PaymentStatus paymentStatus;

    private Double shippingFees;

    private Integer loyaltyPointsApplied;

    private String orderNotes;

    @NotNull(message = "Weight cannot be null")
    private Double weight;

    private Boolean isBreakable = false;

    @Size(max = 500, message = "Special instructions cannot exceed 500 characters")
    private String specialInstructions;
}
