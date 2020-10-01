package com.example;

class Main {

  public static void main(String[] args) throws Exception {
    Service.service(new Subscription[]{
        new Subscription("ad", "fetch-product-page", (body, sender) -> {
          AdifyService adify = new AdifyService(new Adify(new HerokuGetRequest("adify")), body, sender);
          adify.execute();
        })
    });
  }

}
