package org.example.productservice.core.data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Entity
@Table(name = "product")
public class ProductEntity implements Serializable {
    @Id
    private String productId;
    @Column(unique = true)
    private String title;
    private BigDecimal price;
    private Integer quantity;

}
