package com.one2team.codeviz;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import lombok.Getter;

import static java.util.Optional.ofNullable;

public class Metrics {

  @Getter
  private final Map<String, AtomicLong> metrics = new HashMap<> ();

  public void put (String metric, AtomicLong value) {
    metrics.put (metric, value);
  }

  public void set (String metric, long value) {
    metrics.computeIfAbsent (metric, m -> new AtomicLong ()).set (value);
  }

  public void increment (String metric) {
    metrics.computeIfAbsent (metric, m -> new AtomicLong ()).incrementAndGet ();
  }

  public long get (String metric) {
    return ofNullable (metrics.get (metric))
      .map (AtomicLong::get)
      .orElse (0L);
  }

  public void add (String metric, long value) {
    metrics.computeIfAbsent (metric, m -> new AtomicLong ()).addAndGet (value);
  }
}
