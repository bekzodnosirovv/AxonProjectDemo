package org.example.productservice.core.data;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<ProductEntity, String> {
    ProductEntity findByProductId(String productId);
}
