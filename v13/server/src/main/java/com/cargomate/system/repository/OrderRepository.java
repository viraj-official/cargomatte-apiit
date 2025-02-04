package com.cargomate.system.repository;

import com.cargomate.system.model.Customer;
import com.cargomate.system.model.Order;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends CrudRepository<Order, Long> {
    Optional<Order> findByTrackingNumber(String trackingNumber);
    List<Order> findByCustomer(Customer customer);
}