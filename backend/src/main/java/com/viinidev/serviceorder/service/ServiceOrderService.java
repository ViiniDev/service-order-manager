package com.viinidev.serviceorder.service;

import com.viinidev.serviceorder.domain.*;
import com.viinidev.serviceorder.dto.*;
import com.viinidev.serviceorder.repository.ServiceOrderRepository;
import com.viinidev.serviceorder.repository.UserRepository;
import com.viinidev.serviceorder.security.CurrentUserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ServiceOrderService {

    private final ServiceOrderRepository serviceOrderRepository;
    private final UserRepository userRepository;
    private final CurrentUserService currentUserService;

    public ServiceOrderService(
            ServiceOrderRepository serviceOrderRepository,
            UserRepository userRepository,
            CurrentUserService currentUserService
    ) {
        this.serviceOrderRepository = serviceOrderRepository;
        this.userRepository = userRepository;
        this.currentUserService = currentUserService;
    }

    @Transactional
    public ServiceOrderResponse create(CreateOrderRequest request) {
        User client = currentUserService.get();
        ServiceOrder order = serviceOrderRepository.save(new ServiceOrder(
                request.title().trim(),
                request.description().trim(),
                request.priority(),
                client
        ));
        return ServiceOrderResponse.from(order);
    }

    @Transactional(readOnly = true)
    public List<ServiceOrderResponse> list(OrderStatus status) {
        User user = currentUserService.get();
        List<ServiceOrder> orders = switch (user.getRole()) {
            case ADMIN -> status == null ? serviceOrderRepository.findAll() : serviceOrderRepository.findByStatus(status);
            case TECHNICIAN -> serviceOrderRepository.findByTechnician(user);
            case CLIENT -> serviceOrderRepository.findByClient(user);
        };
        return orders.stream().map(ServiceOrderResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public ServiceOrderResponse getById(Long id) {
        ServiceOrder order = findVisibleOrder(id);
        return ServiceOrderResponse.from(order);
    }

    @Transactional
    public ServiceOrderResponse assign(Long id, AssignOrderRequest request) {
        User current = currentUserService.get();
        if (current.getRole() != Role.ADMIN) {
            throw new IllegalArgumentException("Only admins can assign service orders.");
        }
        User technician = userRepository.findById(request.technicianId())
                .filter(user -> user.getRole() == Role.TECHNICIAN)
                .orElseThrow(() -> new IllegalArgumentException("Technician not found."));
        ServiceOrder order = serviceOrderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Service order not found."));
        order.assignTo(technician);
        return ServiceOrderResponse.from(serviceOrderRepository.save(order));
    }

    @Transactional
    public ServiceOrderResponse updateStatus(Long id, UpdateStatusRequest request) {
        ServiceOrder order = findVisibleOrder(id);
        User current = currentUserService.get();
        if (current.getRole() == Role.CLIENT) {
            throw new IllegalArgumentException("Clients cannot update service order status.");
        }
        order.updateStatus(request.status());
        return ServiceOrderResponse.from(serviceOrderRepository.save(order));
    }

    @Transactional
    public ServiceOrderResponse addComment(Long id, CommentRequest request) {
        ServiceOrder order = findVisibleOrder(id);
        User author = currentUserService.get();
        order.addComment(new OrderComment(request.message().trim(), author, order));
        return ServiceOrderResponse.from(serviceOrderRepository.save(order));
    }

    @Transactional(readOnly = true)
    public DashboardResponse dashboard() {
        return new DashboardResponse(
                serviceOrderRepository.count(),
                serviceOrderRepository.countByStatus(OrderStatus.OPEN),
                serviceOrderRepository.countByStatus(OrderStatus.ASSIGNED),
                serviceOrderRepository.countByStatus(OrderStatus.IN_PROGRESS),
                serviceOrderRepository.countByStatus(OrderStatus.RESOLVED),
                serviceOrderRepository.countByStatus(OrderStatus.CLOSED)
        );
    }

    private ServiceOrder findVisibleOrder(Long id) {
        ServiceOrder order = serviceOrderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Service order not found."));
        User user = currentUserService.get();
        boolean visible = switch (user.getRole()) {
            case ADMIN -> true;
            case TECHNICIAN -> order.getTechnician() != null && order.getTechnician().getId().equals(user.getId());
            case CLIENT -> order.getClient().getId().equals(user.getId());
        };
        if (!visible) {
            throw new IllegalArgumentException("Service order is not available for this user.");
        }
        return order;
    }
}
