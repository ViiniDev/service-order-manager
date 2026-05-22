package com.viinidev.serviceorder.controller;

import com.viinidev.serviceorder.dto.DashboardResponse;
import com.viinidev.serviceorder.service.ServiceOrderService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final ServiceOrderService serviceOrderService;

    public DashboardController(ServiceOrderService serviceOrderService) {
        this.serviceOrderService = serviceOrderService;
    }

    @GetMapping
    public DashboardResponse dashboard() {
        return serviceOrderService.dashboard();
    }
}
