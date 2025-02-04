package com.cargomate.system.dtos.request;

import com.cargomate.system.enums.OrderStatus;
import com.cargomate.system.enums.PaymentMethod;
import com.cargomate.system.enums.PaymentStatus;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Future;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Getter
@Setter
public class UpdateOrderRequest {

    @Size(max = 255, message = "Shipment address cannot exceed 255 characters")
    private String shipmentAddress;

    @Future(message = "New delivery date must be in the future")
    private LocalDateTime deliveryDate;

    private OrderStatus status;

    private PaymentMethod paymentMethod;

    private PaymentStatus paymentStatus;

    private Double shippingFees;

    private Double weight;

    private Integer loyaltyPointsApplied;

    private String orderNotes;

    private Boolean isBreakable;

    @Size(max = 500, message = "Special instructions cannot exceed 500 characters")
    private String specialInstructions;
}
