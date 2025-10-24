package com.example.mongodbdemo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface ProductRepository extends MongoRepository<Product, String> {
  Optional<Product> findBySku(String sku);
  List<Product> findByNameRegex(String regex);
  Page<Product> findByPriceBetween(BigDecimal min, BigDecimal max, Pageable pageable);
}
