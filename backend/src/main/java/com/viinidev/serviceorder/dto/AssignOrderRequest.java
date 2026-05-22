package com.viinidev.serviceorder.dto;

import jakarta.validation.constraints.NotNull;

public record AssignOrderRequest(@NotNull Long technicianId) {
}
