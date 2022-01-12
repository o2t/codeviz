package com.one2team.codeviz;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@EqualsAndHashCode (of = "name")
@RequiredArgsConstructor
public class Node {

  @Getter
  private final String name;

  @Getter
  @Setter
  private Set<String> dependencies;

  @Getter
  @Setter
  private long size;

  @Getter
  @Setter
  private Map<String, AtomicLong> metrics = new HashMap<> ();

}
