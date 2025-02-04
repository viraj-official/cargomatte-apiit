package com.cargomate.system.dtos.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderItemRequest {

    private Long productId;

    private Integer quantity;

    private Double pricePerItem;
}
