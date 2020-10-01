package com.example;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

class Adify {
  private final ExternalService service;

  Adify(ExternalService service) {
    this.service = service;
  }

  String fetch(String productId) {
    try {
      String content = service.get("?product=" + productId);
      JSONObject obj = (JSONObject) new JSONParser().parse(content);
      return (String) obj.get("product");
    } catch (Exception e) {
      return "";
    }
  }
}
