package org.example.model;

import org.example.util.TransactionType;

import java.time.Instant;

public record Transaction(Long id, TransactionType type, Double size, String loginPlayer, Instant createdTime) {
}
