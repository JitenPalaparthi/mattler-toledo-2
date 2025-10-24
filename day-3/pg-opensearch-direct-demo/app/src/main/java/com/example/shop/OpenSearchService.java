package com.example.shop;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@Service
public class OpenSearchService {

  private final WebClient web;
  private final ObjectMapper mapper;

  @Value("${opensearch.url}")
  private String osUrl;

  @Value("${opensearch.index:products}")
  private String index;

  public OpenSearchService(WebClient.Builder builder, ObjectMapper mapper) {
    this.web = builder.build();
    this.mapper = mapper;
  }

  public Mono<Void> ensureIndex() {
    String headUrl = osUrl + "/" + index;
    return web.get().uri(headUrl)
      .exchangeToMono(resp -> {
        if (resp.statusCode().is2xxSuccessful()) return Mono.empty();
        String createUrl = osUrl + "/" + index;
        Map<String, Object> body = Map.of(
          "mappings", Map.of("properties", Map.of(
            "name", Map.of("type","text"),
            "category", Map.of("type","keyword"),
            "price", Map.of("type","double"),
            "description", Map.of("type","text")
          ))
        );
        return web.put().uri(createUrl).contentType(MediaType.APPLICATION_JSON)
          .bodyValue(body).retrieve().bodyToMono(String.class).then();
      });
  }

  public Mono<Void> indexProduct(Product p) {
    String url = osUrl + "/" + index + "/_doc/" + p.getId();
    Map<String, Object> doc = Map.of(
      "id", p.getId(),
      "name", p.getName(),
      "category", p.getCategory(),
      "price", p.getPrice(),
      "description", p.getDescription()
    );
    return web.put().uri(url).contentType(MediaType.APPLICATION_JSON)
      .bodyValue(doc).retrieve().bodyToMono(String.class).then();
  }

  public Mono<String> search(String q, int size) {
    String url = osUrl + "/" + index + "/_search";
    Map<String, Object> payload = Map.of("query", Map.of("query_string", Map.of("query", q)), "size", size);
    return web.post().uri(url).contentType(MediaType.APPLICATION_JSON)
      .bodyValue(payload).retrieve().bodyToMono(String.class);
  }
}
