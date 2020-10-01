package com.example;

class AdifyService {
  private final Adify gate;
  private final String body;
  private final Subscription.Sender sender;

  AdifyService(Adify gate, String body, Subscription.Sender sender) {
    this.gate = gate;
    this.body = body;
    this.sender = sender;
  }

  void execute() {
    System.out.println("ad: fetch-product-page");
    String[] buffer = body.split(",");
    String sessionId = buffer[0];
    String userId = buffer[1];
    String productId = buffer[2];
    String product = gate.fetch(productId);
    sender.send("display", "SESSION_ID,advert,PRODUCT_ID," + product);
  }
}
