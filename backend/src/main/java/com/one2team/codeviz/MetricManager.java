package com.one2team.codeviz;

import com.github.javaparser.ast.CompilationUnit;

public interface MetricManager {

  void collect (CompilationUnit unit, Node node);

  void collect (Graph graph);

}
