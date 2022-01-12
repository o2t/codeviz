package com.one2team.codeviz.utils;

import java.util.Iterator;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static java.util.Objects.requireNonNull;

public class Streams {

  public static <T> Stream<T> of (Iterator<T> iterator) {
    Iterable<T> i = () -> iterator;
    return StreamSupport.stream (i.spliterator (), false);
  }


  public static <T> Stream<T> recursing (T first, Function<T, T> recursion) {
    requireNonNull (recursion);
    return of (new Iterator<T> () {

      private T next = first;

      @Override
      public boolean hasNext () {
        return next != null;
      }

      @Override
      public T next () {
        ensureHasNext ();
        T ret = next;
        next = recursion.apply (next);
        return ret;
      }

      private void ensureHasNext () {
        if (next == null)
          throw new IllegalStateException ();
      }
    });
  }

  public static Stream<Class<?>> superClassesOf (Class<?> klass) {
    return recursing (klass, Class::getSuperclass);
  }
}
