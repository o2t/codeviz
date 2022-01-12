package com.one2team.codeviz;

import com.github.javaparser.ast.CompilationUnit;

public interface PluginsManager {

  void analyze (CompilationUnit unit, Node node);

  void analyze (Graph graph);

}
