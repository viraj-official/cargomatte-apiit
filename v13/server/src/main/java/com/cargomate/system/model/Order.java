package com.cargomate.system.model;

import com.cargomate.system.enums.OrderStatus;
import com.cargomate.system.enums.PaymentMethod;
import com.cargomate.system.enums.PaymentStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    @JsonIgnore
    private Customer customer;

    @Column(nullable = false)
    private String shipmentAddress;

    @Column(nullable = false)
    private LocalDateTime deliveryDate;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @Column(nullable = false, unique = true)
    private String trackingNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus paymentStatus;

    @Column(nullable = true)
    private Double shippingFees;

    @Column(nullable = true)
    private Integer loyaltyPointsApplied;

    @Column(nullable = true)
    private String orderNotes;

    @Column(nullable = false)
    private LocalDateTime createdDate;

    @Column(nullable = true)
    private LocalDateTime modifiedDate;

    @Column(nullable = true)
    private LocalDateTime completedDate;

    @Column(nullable = false)
    private Double weight;

    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean isBreakable = false;

    @Column(nullable = true, length = 500)
    private String specialInstructions;
}
