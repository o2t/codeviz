package com.one2team.codeviz;

import com.github.javaparser.ast.CompilationUnit;
import com.one2team.codeviz.config.MetricConfig;

public class MetricCollector<CONFIG extends MetricConfig, CONTEXT> {

  public CONTEXT newContext (CONFIG config) {
    return null;
  }

  public void collect (CONFIG config, CONTEXT context, CompilationUnit unit, Node node) {
  }

  public void collect (CONFIG config, CONTEXT context, Graph graph) {
  }
}
