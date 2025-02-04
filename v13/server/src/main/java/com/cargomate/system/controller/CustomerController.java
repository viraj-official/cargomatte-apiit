package com.cargomate.system.controller;

import com.cargomate.security.exception.ResourceNotFoundException;
import com.cargomate.security.service.UserService;
import com.cargomate.system.dtos.request.CreateOrderRequest;
import com.cargomate.system.dtos.request.UpdateCustomerRequest;
import com.cargomate.system.dtos.request.UpdateOrderRequest;
import com.cargomate.system.dtos.response.OrderResponseDTO;
import com.cargomate.system.model.Customer;
import com.cargomate.system.model.Order;
import com.cargomate.system.service.CustomerService;
import com.cargomate.system.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@Slf4j
@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;
    private final OrderService orderService;
    private final UserService userService;

    @PutMapping("/update")
    public ResponseEntity<Customer> updateCustomer(@RequestBody @Valid UpdateCustomerRequest request) {
        log.info("Initiating customer update for email: {}", userService.curentUser().getUsername());

        try {
            Customer updatedCustomer = customerService.updateCustomer(request);
            log.info("Successfully updated customer with ID: {}", updatedCustomer.getId());
            return ResponseEntity.ok(updatedCustomer);
        } catch (ResourceNotFoundException e) {
            log.error("Customer not found for email: {}", userService.curentUser().getUsername(), e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            log.error("Unexpected error while updating customer with email: {}", userService.curentUser().getUsername(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping("/order")
    public ResponseEntity<OrderResponseDTO> createOrder(@RequestBody @Valid CreateOrderRequest request) {
        log.info("Creating a new order for customer: {}", userService.curentUser().getUsername());

        try {
            OrderResponseDTO response = orderService.createOrder(request);
            log.info("Successfully created order with tracking number: {}", response.getTrackingNumber());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            log.error("Error creating order for customer: {}", userService.curentUser().getUsername(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PutMapping("/order/{orderId}")
    public ResponseEntity<OrderResponseDTO> updateOrder(@PathVariable Long orderId,
                                                        @RequestBody @Valid UpdateOrderRequest request) {
        log.info("Updating order with ID: {}", orderId);

        try {
            OrderResponseDTO response = orderService.updateOrder(orderId, request);
            log.info("Successfully updated order with ID: {}", orderId);
            return ResponseEntity.ok(response);
        } catch (ResourceNotFoundException e) {
            log.error("Order not found with ID: {}", orderId, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            log.error("Unexpected error while updating order with ID: {}", orderId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/track/{trackingNumber}")
    public ResponseEntity<OrderResponseDTO> trackOrder(@PathVariable String trackingNumber) {
        log.info("Tracking order with tracking number: {}", trackingNumber);

        try {
            OrderResponseDTO response = orderService.trackOrder(trackingNumber);
            log.info("Successfully tracked order with tracking number: {}", trackingNumber);
            return ResponseEntity.ok(response);
        } catch (ResourceNotFoundException e) {
            log.error("Order not found for tracking number: {}", trackingNumber, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            log.error("Unexpected error while tracking order with tracking number: {}", trackingNumber, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // Fetch all orders for a customer
    @GetMapping("/order")
    public ResponseEntity<List<OrderResponseDTO>> allOrders() {
        log.info("Fetching all orders for customer: {}", userService.curentUser().getUsername());

        try {
            List<OrderResponseDTO> orders = orderService.getAllOrdersForCustomer();
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            log.error("Error fetching orders for customer: {}", userService.curentUser().getUsername(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // Fetch a specific order by its orderId
    @GetMapping("/order/{orderId}")
    public ResponseEntity<OrderResponseDTO> order(@PathVariable String orderId) {
        log.info("Fetching order with ID: {}", orderId);

        try {
            OrderResponseDTO order = orderService.getOrderById(orderId);
            return ResponseEntity.ok(order);
        } catch (ResourceNotFoundException e) {
            log.error("Order not found with ID: {}", orderId, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            log.error("Error fetching order with ID: {}", orderId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
