package com.example;

class Subscription {

  public interface Sender {
    void send(String event, String body);
  }

  public interface Handler {
    void apply(String body, Sender send);
  }

  final String river;
  final String event;
  final Handler handle;

  Subscription(String river, String event, Handler handle) {
    this.river = river;
    this.event = event;
    this.handle = handle;
  }

}
