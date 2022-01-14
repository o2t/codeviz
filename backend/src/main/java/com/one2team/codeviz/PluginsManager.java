package com.one2team.codeviz;

import com.github.javaparser.ast.CompilationUnit;

public interface PluginsManager {

  void analyze1 (CompilationUnit unit, Node node);

  void analyze2 (CompilationUnit unit, Node node);

  void analyze ();

}
