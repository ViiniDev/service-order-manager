package com.viinidev.serviceorder.dto;

import com.viinidev.serviceorder.domain.OrderPriority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateOrderRequest(
        @NotBlank @Size(max = 120) String title,
        @NotBlank @Size(max = 1200) String description,
        @NotNull OrderPriority priority
) {
}
