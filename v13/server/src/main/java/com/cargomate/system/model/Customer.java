package com.cargomate.system.model;

import com.cargomate.security.model.User;
import com.cargomate.system.enums.AccountStatus;
import com.cargomate.system.enums.NotificationPreference;
import com.cargomate.system.enums.PaymentMethod;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "customers")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String firstName;

    @Column(nullable = false, length = 50)
    private String lastName;

    @Column(length = 50)
    private String displayName;

    @Column(unique = true, nullable = false, length = 100)
    private String email;

    @Column(nullable = false, length = 15)
    private String phoneNumber;

    @Column(nullable = false, length = 255)
    private String address;

    @Column(precision = 10, scale = 6)
    private Double latitude;

    @Column(precision = 10, scale = 6)
    private Double longitude;

    @Column(length = 20)
    private LocalDateTime preferredDeliveryTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountStatus accountStatus = AccountStatus.ACTIVE;

    @Enumerated(EnumType.STRING)
    private NotificationPreference notificationPreference = NotificationPreference.PUSH_NOTIFICATION;

    @Column(nullable = false)
    private int loyaltyPoints = 0;

    @Enumerated(EnumType.STRING)
    private PaymentMethod defaultPaymentMethod = PaymentMethod.CARD;

    @Column
    private LocalDateTime lastLogin;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "customer")
    private List<Order> orders;
}
