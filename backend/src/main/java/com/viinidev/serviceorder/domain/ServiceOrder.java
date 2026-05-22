package com.viinidev.serviceorder.domain;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class ServiceOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, length = 1200)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderPriority priority;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status = OrderStatus.OPEN;

    @ManyToOne(optional = false)
    private User client;

    @ManyToOne
    private User technician;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    private LocalDateTime closedAt;

    @OneToMany(mappedBy = "serviceOrder", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("createdAt ASC")
    private List<OrderComment> comments = new ArrayList<>();

    protected ServiceOrder() {
    }

    public ServiceOrder(String title, String description, OrderPriority priority, User client) {
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.client = client;
    }

    public void assignTo(User technician) {
        this.technician = technician;
        this.status = OrderStatus.ASSIGNED;
        touch();
    }

    public void updateStatus(OrderStatus status) {
        this.status = status;
        this.closedAt = status == OrderStatus.CLOSED ? LocalDateTime.now() : null;
        touch();
    }

    public void addComment(OrderComment comment) {
        comments.add(comment);
        touch();
    }

    private void touch() {
        this.updatedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public OrderPriority getPriority() {
        return priority;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public User getClient() {
        return client;
    }

    public User getTechnician() {
        return technician;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public LocalDateTime getClosedAt() {
        return closedAt;
    }

    public List<OrderComment> getComments() {
        return comments;
    }
}
