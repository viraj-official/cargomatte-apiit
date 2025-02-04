package com.cargomate.system.dtos.response;

import com.cargomate.system.enums.OrderStatus;
import com.cargomate.system.enums.PaymentMethod;
import com.cargomate.system.enums.PaymentStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponseDTO {

    private Long id;
    private String shipmentAddress;
    private LocalDateTime deliveryDate;
    private OrderStatus status;
    private String trackingNumber;
    private PaymentMethod paymentMethod;
    private PaymentStatus paymentStatus;
    private Double shippingFees;
    private Integer loyaltyPointsApplied;
    private String orderNotes;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;
    private LocalDateTime completedDate;
    private Double weight;
    private Boolean isBreakable;
    private String specialInstructions;
}
