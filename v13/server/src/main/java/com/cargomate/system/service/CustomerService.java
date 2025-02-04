package com.cargomate.system.service;

import com.cargomate.security.exception.ResourceNotFoundException;
import com.cargomate.security.service.UserService;
import com.cargomate.system.dtos.request.UpdateCustomerRequest;
import com.cargomate.system.model.Customer;
import com.cargomate.system.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final UserService userService;

    public Customer updateCustomer(UpdateCustomerRequest request) {
        Customer customer = customerRepository.findByEmail(userService.curentUser().getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with email: " + userService.curentUser().getUsername()));

        if (request.getPhoneNumber() != null) customer.setPhoneNumber(request.getPhoneNumber());
        if (request.getAddress() != null) customer.setAddress(request.getAddress());
        if (request.getDisplayName() != null) customer.setDisplayName(request.getDisplayName());
        if (request.getFirstName() != null) customer.setFirstName(request.getFirstName());
        if (request.getLastName() != null) customer.setLastName(request.getLastName());
        if (request.getLatitude() != null) customer.setLatitude(request.getLatitude());
        if (request.getLongitude() != null) customer.setLongitude(request.getLongitude());
        if (request.getPreferredDeliveryTime() != null) customer.setPreferredDeliveryTime(request.getPreferredDeliveryTime());
        if (request.getAccountStatus() != null) customer.setAccountStatus(request.getAccountStatus());
        if (request.getNotificationPreference() != null) customer.setNotificationPreference(request.getNotificationPreference());
        if (request.getLoyaltyPoints() != null) customer.setLoyaltyPoints(request.getLoyaltyPoints());
        if (request.getDefaultPaymentMethod() != null) customer.setDefaultPaymentMethod(request.getDefaultPaymentMethod());
        if (request.getLastLogin() != null) customer.setLastLogin(request.getLastLogin());

        return customerRepository.save(customer);
    }
}
