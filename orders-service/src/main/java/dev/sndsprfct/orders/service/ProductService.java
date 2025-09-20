package dev.sndsprfct.orders.service;

import dev.sndsprfct.orders.entity.Product;
import dev.sndsprfct.orders.exception.ProductsNotAvailableException;
import dev.sndsprfct.orders.exception.ProductsNotFoundException;
import dev.sndsprfct.orders.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;

    public Set<Product> checkProductsAvailability(Set<Long> requiredProductIds) {
        Set<Product> foundProducts = new HashSet<>(productRepository.findAllById(requiredProductIds));
        if (foundProducts.size() != requiredProductIds.size()) {
            throw new ProductsNotFoundException();
        }

        List<Product> notAvailableProducts = foundProducts.stream()
                .filter(product -> !product.getIsAvailable())
                .toList();
        if (!notAvailableProducts.isEmpty()) {
            throw new ProductsNotAvailableException();
        }
        return foundProducts;
    }
}
