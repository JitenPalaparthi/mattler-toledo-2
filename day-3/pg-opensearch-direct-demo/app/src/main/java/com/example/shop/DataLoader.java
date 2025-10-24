package com.example.shop;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.util.Random;

@Configuration
public class DataLoader {

  @Bean
  CommandLineRunner seed(ProductRepository repo, OpenSearchService os) {
    return args -> {
      os.ensureIndex().block();
      if (repo.count() == 0) {
        Random r = new Random(42);
        String[] cats = {"Books","Electronics","Clothing","Home","Toys"};
        for (int i=1; i<=100; i++) {
          Product p = new Product();
          p.setName("Product " + i);
          p.setCategory(cats[r.nextInt(cats.length)]);
          p.setPrice(BigDecimal.valueOf(5 + r.nextInt(500) + r.nextDouble()));
          p.setDescription("Seed product " + i + " for OpenSearch demo.");
          Product saved = repo.save(p);
          os.indexProduct(saved).block();
        }
        System.out.println("Seeded 100 products and indexed into OpenSearch.");
      } else {
        repo.findAll().forEach(p -> os.indexProduct(p).subscribe());
      }
    };
  }
}
