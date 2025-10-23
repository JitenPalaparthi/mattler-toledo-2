package com.example.mongodbdemo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ProductService {

  private final ProductRepository repo;
  private final MongoTemplate template;

  public ProductService(ProductRepository repo, MongoTemplate template) {
    this.repo = repo;
    this.template = template;
  }

  public Product create(Product p) {
    p.setId(null);
    p.setCreatedAt(Instant.now());
    p.setUpdatedAt(Instant.now());
    return repo.save(p);
  }

  public List<Product> bulkCreate(List<Product> products) {
    Instant now = Instant.now();
    products.forEach(p -> { p.setId(null); p.setCreatedAt(now); p.setUpdatedAt(now); });
    return repo.saveAll(products);
  }

  public Optional<Product> getById(String id) { return repo.findById(id); }
  public Optional<Product> getBySku(String sku) { return repo.findBySku(sku); }

  public Page<Product> search(String nameContains, BigDecimal min, BigDecimal max, int page, int size) {
    Pageable pageable = PageRequest.of(page, size);
    if (min == null) min = BigDecimal.ZERO;
    if (max == null) max = new BigDecimal("999999999");
    if (StringUtils.hasText(nameContains)) {
      Query q = new Query()
          .addCriteria(Criteria.where("name").regex(".*" + nameContains + ".*", "i"))
          .addCriteria(Criteria.where("price").gte(min).lte(max))
          .with(pageable);
      List<Product> content = template.find(q, Product.class);
      long total = template.count(Query.of(q).limit(-1).skip(-1), Product.class);
      return new org.springframework.data.domain.PageImpl<>(content, pageable, total);
    } else {
      return repo.findByPriceBetween(min, max, pageable);
    }
  }

  public Optional<Product> replace(String id, Product p) {
    return repo.findById(id).map(existing -> {
      p.setId(id);
      p.setCreatedAt(existing.getCreatedAt());
      p.setUpdatedAt(Instant.now());
      return repo.save(p);
    });
  }

  public Optional<Product> patch(String id, Map<String, Object> fields) {
    Query q = new Query(Criteria.where("_id").is(id));
    Update u = new Update();
    fields.forEach((k,v) -> {
      if (!"id".equals(k) && !"createdAt".equals(k)) u.set(k, v);
    });
    u.set("updatedAt", Instant.now());
    var res = template.updateFirst(q, u, Product.class);
    if (res.getModifiedCount() > 0) {
      return Optional.ofNullable(template.findById(id, Product.class));
    }
    return Optional.empty();
  }

  public boolean delete(String id) {
    if (repo.existsById(id)) {
      repo.deleteById(id);
      return true;
    }
    return false;
  }
}
