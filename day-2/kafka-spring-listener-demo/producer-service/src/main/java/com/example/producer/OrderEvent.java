
package com.example.producer;

public record OrderEvent(String orderId, String item,double price) { }
