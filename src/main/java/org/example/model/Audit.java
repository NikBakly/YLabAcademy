package org.example.model;

import org.example.util.AuditType;

import java.time.Instant;

public record Audit(Long id, AuditType type, String loginPlayer, Instant createdTime) {
}
