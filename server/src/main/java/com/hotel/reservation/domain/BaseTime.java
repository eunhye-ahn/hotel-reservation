package com.hotel.reservation.domain;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@EntityListeners(AuditingEntityListener.class)
@MappedSuperclass
public class BaseTime {
    @CreatedDate
    @Column(nullable = false, name="created_at")
    protected LocalDateTime createdAT;

    @LastModifiedDate
    @Column(nullable = false, name = "updated_at")
    protected LocalDateTime updatedAt;
}
