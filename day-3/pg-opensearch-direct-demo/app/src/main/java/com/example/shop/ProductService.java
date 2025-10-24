package com.example.shop;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductService {

  private final ProductRepository repo;
  private final OpenSearchService os;

  public ProductService(ProductRepository repo, OpenSearchService os) {
    this.repo = repo;
    this.os = os;
  }

  @Transactional
  public Product create(Product p) {
    Product saved = repo.save(p);
    os.indexProduct(saved).subscribe();
    return saved;
  }

  @Transactional
  public Product update(Long id, Product patch) {
    Product p = repo.findById(id).orElseThrow();
    if (patch.getName()!=null) p.setName(patch.getName());
    if (patch.getCategory()!=null) p.setCategory(patch.getCategory());
    if (patch.getPrice()!=null) p.setPrice(patch.getPrice());
    if (patch.getDescription()!=null) p.setDescription(patch.getDescription());
    Product saved = repo.save(p);
    os.indexProduct(saved).subscribe();
    return saved;
  }
}
