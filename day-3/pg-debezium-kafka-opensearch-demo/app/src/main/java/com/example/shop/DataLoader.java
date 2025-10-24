package com.example.shop;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.util.Random;

@Configuration
public class DataLoader {

  @Bean
  CommandLineRunner seed(ProductRepository repo) {
    return args -> {
      if (repo.count() > 0) return;
      Random r = new Random(42);
      String[] cats = {"Books","Electronics","Clothing","Home","Toys"};
      for (int i=1; i<=100; i++) {
        Product p = new Product();
        p.setName("Product " + i);
        p.setCategory(cats[r.nextInt(cats.length)]);
        p.setPrice(BigDecimal.valueOf(5 + r.nextInt(500) + r.nextDouble()));
        p.setDescription("Sample product " + i + " generated for Debezium/OpenSearch demo.");
        repo.save(p);
      }
      System.out.println("Seeded 100 products into Postgres.");
    };
  }
}
