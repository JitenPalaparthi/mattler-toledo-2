package com.example.tracingdemo;
public record Order(long id, String customer, String sku, int qty, int priceCents) {}
