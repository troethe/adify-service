package com.example;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AdifyServiceTest {

  class SenderSpy implements Subscription.Sender {
    String event;
    String body;

    @Override
    public void send(String event, String body) {
      this.event = event;
      this.body = body;
    }
  }

  @Test
  @Tag("slow")
  void foo() {
    SenderSpy spy = new SenderSpy();
    AdifyService a = new AdifyService(new Adify(new HerokuGetRequest("adify")), "SESSION_ID,USER_ID,PRODUCT_ID", spy);
    a.execute();
    assertEquals("display", spy.event);
  }

  public class MockGetRequest implements ExternalService {

	  MockGetRequest(String service) {}

	  @Override
	  public String get(String arg) throws IOException {
		  return "{\"product-name\":\"PRODUCT_NAME\"}";
	  }
  }

  @Test
  @Tag("slow")
  void bar1() {
    SenderSpy spy = new SenderSpy();
    AdifyService a = new AdifyService(new Adify(new HerokuGetRequest("adify")), "SESSION_ID,USER_ID,PRODUCT_ID", spy);
    a.execute();
    assertEquals("SESSION_ID,advert,PRODUCT_ID,PRODUCT_NAME", spy.body);
  }

  @Test
  void bar2() {
    SenderSpy spy = new SenderSpy();
    AdifyService a = new AdifyService(new Adify(new MockGetRequest("")), "SESSION_ID,USER_ID,PRODUCT_ID", spy);
    a.execute();
    assertEquals("SESSION_ID,advert,PRODUCT_ID,PRODUCT_NAME", spy.body);
  }

}
