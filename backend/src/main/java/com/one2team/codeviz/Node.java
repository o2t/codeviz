package com.one2team.codeviz;

import java.util.Set;

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

}
