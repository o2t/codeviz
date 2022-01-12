package com.one2team.codeviz;

import java.util.HashSet;
import java.util.Set;

import lombok.Getter;

public class Dependencies {

  @Getter
  private final Set<String> dependencies = new HashSet<> ();


  public void set (Set<String> dependencies) {
    this.dependencies.clear ();
    ;
    this.dependencies.addAll (dependencies);
  }

  public Set<String> get () {
    return dependencies;
  }

  public void add (String dependency) {
    dependencies.add (dependency);
  }
}
