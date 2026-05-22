package com.viinidev.serviceorder.repository;

import com.viinidev.serviceorder.domain.OrderStatus;
import com.viinidev.serviceorder.domain.ServiceOrder;
import com.viinidev.serviceorder.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ServiceOrderRepository extends JpaRepository<ServiceOrder, Long> {
    List<ServiceOrder> findByStatus(OrderStatus status);

    List<ServiceOrder> findByClient(User client);

    List<ServiceOrder> findByTechnician(User technician);

    long countByStatus(OrderStatus status);
}
