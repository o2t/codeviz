package com.one2team.codeviz;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class Injection {

  private static Injector injector;

  public static Injector getInjector () {
    if (injector != null)
      return injector;

    synchronized (Injection.class) {
      return injector != null ?
        injector : (injector = Guice.createInjector (new MainModule ()));
    }
  }
}
