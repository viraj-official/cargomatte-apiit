package com.cargomate.system.dtos.request;

import com.cargomate.system.enums.AccountStatus;
import com.cargomate.system.enums.NotificationPreference;
import com.cargomate.system.enums.PaymentMethod;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.*;
import java.time.LocalDateTime;

@Getter
@Setter
public class UpdateCustomerRequest {

    private Long id;

    @Pattern(regexp = "^\\+?[0-9]{7,15}$", message = "Invalid phone number format")
    private String phoneNumber;

    @Size(max = 255, message = "Address cannot exceed 255 characters")
    private String address;

    @Size(max = 50, message = "Display name cannot exceed 50 characters")
    private String displayName;

    private String firstName;

    private String lastName;

    private Double latitude;

    private Double longitude;

    private LocalDateTime preferredDeliveryTime;

    private AccountStatus accountStatus;

    private NotificationPreference notificationPreference;

    private Integer loyaltyPoints;

    private PaymentMethod defaultPaymentMethod;

    private LocalDateTime lastLogin;
}
