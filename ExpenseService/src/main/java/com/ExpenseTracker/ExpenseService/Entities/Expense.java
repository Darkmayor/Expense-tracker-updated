package com.ExpenseTracker.ExpenseService.Entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@Table(name = "expense" , uniqueConstraints = @UniqueConstraint(columnNames = {"external_id"}))
public class Expense {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "external_id",nullable = false,updatable = false)
    private String externalId;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "amount")
    private BigDecimal amount;

    @Column(name = "merchant")
    private String merchant;

    @Column(name = "currency")
    private String currency;

    @JsonProperty(value = "created_at")
    private Timestamp createdAt;

    @PrePersist
    @PreUpdate
    private void generateExternalId() {
        if(this.createdAt == null){
            this.createdAt = Timestamp.from(Instant.now());
        }
    }


}
