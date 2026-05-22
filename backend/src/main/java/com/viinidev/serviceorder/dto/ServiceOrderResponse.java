package com.viinidev.serviceorder.dto;

import com.viinidev.serviceorder.domain.OrderPriority;
import com.viinidev.serviceorder.domain.OrderStatus;
import com.viinidev.serviceorder.domain.ServiceOrder;

import java.time.LocalDateTime;
import java.util.List;

public record ServiceOrderResponse(
        Long id,
        String title,
        String description,
        OrderPriority priority,
        OrderStatus status,
        UserResponse client,
        UserResponse technician,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime closedAt,
        List<CommentResponse> comments
) {
    public static ServiceOrderResponse from(ServiceOrder order) {
        return new ServiceOrderResponse(
                order.getId(),
                order.getTitle(),
                order.getDescription(),
                order.getPriority(),
                order.getStatus(),
                UserResponse.from(order.getClient()),
                order.getTechnician() == null ? null : UserResponse.from(order.getTechnician()),
                order.getCreatedAt(),
                order.getUpdatedAt(),
                order.getClosedAt(),
                order.getComments().stream().map(CommentResponse::from).toList()
        );
    }
}
