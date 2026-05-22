package com.viinidev.serviceorder.dto;

public record DashboardResponse(
        long total,
        long open,
        long assigned,
        long inProgress,
        long resolved,
        long closed
) {
}
