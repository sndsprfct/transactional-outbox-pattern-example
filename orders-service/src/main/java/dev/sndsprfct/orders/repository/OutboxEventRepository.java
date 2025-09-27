package dev.sndsprfct.orders.repository;

import dev.sndsprfct.orders.entity.outbox.OutboxEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OutboxEventRepository extends JpaRepository<OutboxEvent, Long> {
}
