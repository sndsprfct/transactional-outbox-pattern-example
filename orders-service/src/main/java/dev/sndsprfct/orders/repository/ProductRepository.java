package dev.sndsprfct.orders.repository;

import dev.sndsprfct.orders.entity.orders.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
