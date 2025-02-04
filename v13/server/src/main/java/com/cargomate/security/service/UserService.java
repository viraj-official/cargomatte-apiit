package com.cargomate.security.service;

import com.cargomate.security.model.User;
import com.cargomate.security.request.CreateCustomerRequest;
import com.cargomate.system.model.Customer;

import java.util.List;

public interface UserService {
    Customer saveCustomer(CreateCustomerRequest user);
    User curentUser();
}