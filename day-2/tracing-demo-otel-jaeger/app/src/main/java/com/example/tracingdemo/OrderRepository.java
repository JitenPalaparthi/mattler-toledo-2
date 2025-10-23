package com.example.tracingdemo;
import io.micrometer.observation.annotation.Observed;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
@Repository
public class OrderRepository {
  private final JdbcTemplate jdbc;
  private static final RowMapper<Order> ROW_MAPPER = (rs, i) -> new Order(
      rs.getLong("id"), rs.getString("customer"), rs.getString("sku"),
      rs.getInt("qty"), rs.getInt("price_cents"));
  public OrderRepository(JdbcTemplate jdbc) { this.jdbc = jdbc; }
  @Observed(name="db.order.findById", lowCardinalityKeyValues={"sql","SELECT * FROM orders WHERE id=?"})
  public Optional<Order> findById(long id) {
    List<Order> list = jdbc.query("SELECT id, customer, sku, qty, price_cents FROM orders WHERE id = ?", ROW_MAPPER, id);
    return list.stream().findFirst();
  }
  @Observed(name="db.order.listAll", lowCardinalityKeyValues={"sql","SELECT * FROM orders"})
  public List<Order> findAll() {
    return jdbc.query("SELECT id, customer, sku, qty, price_cents FROM orders ORDER BY id", ROW_MAPPER);
  }
  @Observed(name="db.order.create", lowCardinalityKeyValues={"sql","INSERT INTO orders"})
  public long create(String customer, String sku, int qty, int priceCents) {
    return jdbc.queryForObject("INSERT INTO orders(customer, sku, qty, price_cents) VALUES (?,?,?,?) RETURNING id",
        Long.class, customer, sku, qty, priceCents);
  }
}
