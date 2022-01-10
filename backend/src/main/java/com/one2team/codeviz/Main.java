package com.one2team.codeviz;

import java.io.IOException;

import com.google.inject.Guice;

public class Main {
  public static void main (String ... args) throws IOException {
    Guice.createInjector (new MainModule ())
      .getInstance (Analyzer.class)
      .run (args);
  }
}
