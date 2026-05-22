package com.viinidev.serviceorder.dto;

import com.viinidev.serviceorder.domain.OrderComment;

import java.time.LocalDateTime;

public record CommentResponse(
        Long id,
        String message,
        UserResponse author,
        LocalDateTime createdAt
) {
    public static CommentResponse from(OrderComment comment) {
        return new CommentResponse(
                comment.getId(),
                comment.getMessage(),
                UserResponse.from(comment.getAuthor()),
                comment.getCreatedAt()
        );
    }
}
