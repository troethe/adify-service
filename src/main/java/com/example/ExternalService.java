package com.example;

import java.io.IOException;

public interface ExternalService {
  String get(String arg) throws IOException;
}
