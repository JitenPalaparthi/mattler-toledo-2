package com.example.mongodbdemo;

import jakarta.validation.constraints.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Document("products")
public class Product {
  @Id
  private String id;

  @NotBlank
  private String name;

  @NotBlank
  @Indexed(unique = true)
  private String sku;

  @NotNull
  @DecimalMin(value = "0.00", inclusive = true)
  private BigDecimal price;

  private List<String> tags;

  private Instant createdAt;
  private Instant updatedAt;

  // getters & setters
  public String getId() { return id; }
  public void setId(String id) { this.id = id; }
  public String getName() { return name; }
  public void setName(String name) { this.name = name; }
  public String getSku() { return sku; }
  public void setSku(String sku) { this.sku = sku; }
  public BigDecimal getPrice() { return price; }
  public void setPrice(BigDecimal price) { this.price = price; }
  public List<String> getTags() { return tags; }
  public void setTags(List<String> tags) { this.tags = tags; }
  public Instant getCreatedAt() { return createdAt; }
  public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
  public Instant getUpdatedAt() { return updatedAt; }
  public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
