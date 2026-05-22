package com.viinidev.serviceorder.domain;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class OrderComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 1000)
    private String message;

    @ManyToOne(optional = false)
    private User author;

    @ManyToOne(optional = false)
    private ServiceOrder serviceOrder;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    protected OrderComment() {
    }

    public OrderComment(String message, User author, ServiceOrder serviceOrder) {
        this.message = message;
        this.author = author;
        this.serviceOrder = serviceOrder;
    }

    public Long getId() {
        return id;
    }

    public String getMessage() {
        return message;
    }

    public User getAuthor() {
        return author;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
