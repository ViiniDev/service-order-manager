package com.viinidev.serviceorder.controller;

import com.viinidev.serviceorder.domain.OrderStatus;
import com.viinidev.serviceorder.dto.*;
import com.viinidev.serviceorder.service.ServiceOrderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class ServiceOrderController {

    private final ServiceOrderService serviceOrderService;

    public ServiceOrderController(ServiceOrderService serviceOrderService) {
        this.serviceOrderService = serviceOrderService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ServiceOrderResponse create(@RequestBody @Valid CreateOrderRequest request) {
        return serviceOrderService.create(request);
    }

    @GetMapping
    public List<ServiceOrderResponse> list(@RequestParam(required = false) OrderStatus status) {
        return serviceOrderService.list(status);
    }

    @GetMapping("/{id}")
    public ServiceOrderResponse getById(@PathVariable Long id) {
        return serviceOrderService.getById(id);
    }

    @PatchMapping("/{id}/assign")
    public ServiceOrderResponse assign(@PathVariable Long id, @RequestBody @Valid AssignOrderRequest request) {
        return serviceOrderService.assign(id, request);
    }

    @PatchMapping("/{id}/status")
    public ServiceOrderResponse updateStatus(@PathVariable Long id, @RequestBody @Valid UpdateStatusRequest request) {
        return serviceOrderService.updateStatus(id, request);
    }

    @PostMapping("/{id}/comments")
    public ServiceOrderResponse addComment(@PathVariable Long id, @RequestBody @Valid CommentRequest request) {
        return serviceOrderService.addComment(id, request);
    }
}
