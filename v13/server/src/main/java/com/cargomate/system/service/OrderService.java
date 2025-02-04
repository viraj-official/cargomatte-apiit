package com.cargomate.system.service;

import com.cargomate.security.exception.ResourceNotFoundException;
import com.cargomate.security.service.UserService;
import com.cargomate.system.dtos.request.CreateOrderRequest;
import com.cargomate.system.dtos.request.UpdateOrderRequest;
import com.cargomate.system.dtos.response.OrderResponseDTO;
import com.cargomate.system.enums.OrderStatus;
import com.cargomate.system.enums.PaymentStatus;
import com.cargomate.system.model.Customer;
import com.cargomate.system.model.Order;
import com.cargomate.system.repository.CustomerRepository;
import com.cargomate.system.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserService userService;
    private final CustomerRepository customerRepository;
    private final NotificationService notificationService;

    private static final Double DEFAULT_SHIPPING_FEES = 5.00;
    private static final Integer DEFAULT_LOYALTY_POINTS = 0;
    private static final PaymentStatus DEFAULT_PAYMENT_STATUS = PaymentStatus.PENDING;

    public OrderResponseDTO createOrder(CreateOrderRequest request) {
        Customer customer = customerRepository.findByEmail(userService.curentUser().getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found for email: " + userService.curentUser().getUsername()));

        Double shippingFees = (request.getShippingFees() != null) ? request.getShippingFees() : DEFAULT_SHIPPING_FEES;
        Integer loyaltyPointsApplied = (request.getLoyaltyPointsApplied() != null) ? request.getLoyaltyPointsApplied() : DEFAULT_LOYALTY_POINTS;
        PaymentStatus paymentStatus = (request.getPaymentStatus() != null) ? request.getPaymentStatus() : DEFAULT_PAYMENT_STATUS;

        Order order = Order.builder()
                .customer(customer)
                .shipmentAddress(request.getShipmentAddress())
                .deliveryDate(request.getDeliveryDate())
                .status(OrderStatus.CREATED)
                .trackingNumber(UUID.randomUUID().toString())
                .paymentMethod(request.getPaymentMethod())
                .paymentStatus(paymentStatus)
                .shippingFees(shippingFees)
                .loyaltyPointsApplied(loyaltyPointsApplied)
                .orderNotes(request.getOrderNotes())
                .weight(request.getWeight())
                .createdDate(LocalDateTime.now())
                .isBreakable(request.getIsBreakable() != null ? request.getIsBreakable() : false)
                .specialInstructions(request.getSpecialInstructions() != null ? request.getSpecialInstructions() : "")
                .build();

        order = orderRepository.save(order);

        // Send notifications
        sendOrderNotification(customer, order, "Order Created", "Your order has been successfully created.");

        return toOrderResponseDTO(order);
    }

    public OrderResponseDTO updateOrder(Long orderId, UpdateOrderRequest request) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + orderId));

        // Update order fields based on request
        updateOrderFields(order, request);

        // Send notification
        sendOrderNotification(order.getCustomer(), order, "Order Status Update", "Your order has been updated.");

        order = orderRepository.save(order);

        return toOrderResponseDTO(order);
    }

    private void updateOrderFields(Order order, UpdateOrderRequest request) {
        if (request.getShipmentAddress() != null) order.setShipmentAddress(request.getShipmentAddress());
        if (request.getDeliveryDate() != null) order.setDeliveryDate(request.getDeliveryDate());
        if (request.getPaymentMethod() != null) order.setPaymentMethod(request.getPaymentMethod());
        if (request.getPaymentStatus() != null) order.setPaymentStatus(request.getPaymentStatus());
        if (request.getShippingFees() != null) order.setShippingFees(request.getShippingFees());
        if (request.getLoyaltyPointsApplied() != null) order.setLoyaltyPointsApplied(request.getLoyaltyPointsApplied());
        if (request.getOrderNotes() != null) order.setOrderNotes(request.getOrderNotes());
        if (request.getWeight() != null) order.setWeight(request.getWeight());
        order.setIsBreakable(request.getIsBreakable() != null ? request.getIsBreakable() : order.getIsBreakable());
        order.setSpecialInstructions(request.getSpecialInstructions() != null ? request.getSpecialInstructions() : order.getSpecialInstructions());
        order.setModifiedDate(LocalDateTime.now());

        if (request.getStatus() != null) {
            order.setStatus(request.getStatus());
            if (request.getStatus() == OrderStatus.COMPLETED) {
                order.setCompletedDate(LocalDateTime.now());
            }
        }
    }

    public OrderResponseDTO trackOrder(String trackingNumber) {
        Order order = orderRepository.findByTrackingNumber(trackingNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found for tracking number: " + trackingNumber));

        return toOrderResponseDTO(order);
    }
    public List<OrderResponseDTO> getAllOrdersForCustomer() {
        // Get the current customer by their email
        Customer customer = customerRepository.findByEmail(userService.curentUser().getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        // Fetch all orders for the customer
        List<Order> orders = orderRepository.findByCustomer(customer);

        // Convert orders to DTOs and return
        return orders.stream()
                .map(this::toOrderResponseDTO)
                .collect(Collectors.toList());
    }

    // Fetch an order by ID
    public OrderResponseDTO getOrderById(String orderId) {
        // Fetch the order from the repository
        Order order = orderRepository.findById(Long.parseLong(orderId))
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + orderId));

        // Return the order DTO
        return toOrderResponseDTO(order);
    }

    private void sendOrderNotification(Customer customer, Order order, String subject, String introMessage) {
        // Define the date-time format pattern
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        StringBuilder message = new StringBuilder(introMessage + "\n\n");

        message.append("Order ID: ").append(order.getId()).append("\n");
        message.append("Tracking Number: ").append(order.getTrackingNumber()).append("\n");
        message.append("Shipment Address: ").append(order.getShipmentAddress()).append("\n");

        // Format and append the delivery date
        if (order.getDeliveryDate() != null) {
            String formattedDeliveryDate = order.getDeliveryDate().format(formatter);
            message.append("Expected Delivery Date: ").append(formattedDeliveryDate).append("\n");
        }

        message.append("Payment Method: ").append(order.getPaymentMethod()).append("\n");
        message.append("Payment Status: ").append(order.getPaymentStatus()).append("\n");
        message.append("Shipping Fees: $").append(order.getShippingFees()).append("\n");

        if (order.getLoyaltyPointsApplied() != null) {
            message.append("Loyalty Points Used: ").append(order.getLoyaltyPointsApplied()).append("\n");
        }
        if (order.getWeight() != null) {
            message.append("Package Weight: ").append(order.getWeight()).append(" kg\n");
        }
        message.append("Is Breakable: ").append(order.getIsBreakable() ? "Yes" : "No").append("\n");

        if (order.getSpecialInstructions() != null && !order.getSpecialInstructions().isEmpty()) {
            message.append("Special Instructions: ").append(order.getSpecialInstructions()).append("\n");
        }

        if (order.getStatus() == OrderStatus.COMPLETED) {
            message.append("\nYour order has been completed on: ").append(order.getCompletedDate()).append("\n");
        }

        message.append("\nThank you for choosing CargoMate!");

        // Send the email and SMS notifications
//        notificationService.sendEmail(customer.getEmail(), subject, message.toString().replace("\n", "<br>"));
//        notificationService.sendSMS(customer.getPhoneNumber(), message.toString());
    }

    private OrderResponseDTO toOrderResponseDTO(Order order) {
        return OrderResponseDTO.builder()
                .id(order.getId())
                .shipmentAddress(order.getShipmentAddress())
                .deliveryDate(order.getDeliveryDate())
                .status(order.getStatus())
                .trackingNumber(order.getTrackingNumber())
                .paymentMethod(order.getPaymentMethod())
                .paymentStatus(order.getPaymentStatus())
                .shippingFees(order.getShippingFees())
                .loyaltyPointsApplied(order.getLoyaltyPointsApplied())
                .orderNotes(order.getOrderNotes())
                .createdDate(order.getCreatedDate())
                .modifiedDate(order.getModifiedDate())
                .weight(order.getWeight())
                .completedDate(order.getCompletedDate())
                .isBreakable(order.getIsBreakable())
                .specialInstructions(order.getSpecialInstructions())
                .build();
    }


}
