package com.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

class HerokuGetRequest implements ExternalService {
  private final String service;

  HerokuGetRequest(String service) {
    this.service = service;
  }

  @Override
  public String get(String arg) throws IOException {
    URL url = new URL("https://" + service + ".herokuapp.com/" + arg);
    HttpURLConnection con = (HttpURLConnection) url.openConnection();
    con.setRequestMethod("GET");
    return getResponseString(con);
  }

  private String getResponseString(HttpURLConnection con) throws IOException {
    BufferedReader in = new BufferedReader(
        new InputStreamReader(con.getInputStream()));
    String inputLine;
    StringBuilder content = new StringBuilder();
    while ((inputLine = in.readLine()) != null) {
      content.append(inputLine);
    }
    in.close();
    return content.toString();
  }
}
