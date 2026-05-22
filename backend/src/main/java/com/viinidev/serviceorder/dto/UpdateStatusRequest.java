package com.viinidev.serviceorder.dto;

import com.viinidev.serviceorder.domain.OrderStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateStatusRequest(@NotNull OrderStatus status) {
}
