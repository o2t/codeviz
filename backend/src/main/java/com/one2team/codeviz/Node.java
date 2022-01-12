package com.one2team.codeviz;

import com.one2team.codeviz.utils.AttributeContainer;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@EqualsAndHashCode (of = "name", callSuper = true)
public class Node extends AttributeContainer {

  @Getter
  private final String name;

  public Metrics getMetrics () {
    return getOrCreateAttribute (Metrics.class);
  }

  public Dependencies getDependencies () {
    return getOrCreateAttribute (Dependencies.class);
  }
}

