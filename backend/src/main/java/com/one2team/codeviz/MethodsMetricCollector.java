package com.one2team.codeviz;

import java.util.concurrent.atomic.AtomicLong;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.one2team.codeviz.config.MethodsMetricConfig;

public class MethodsMetricCollector extends MetricCollector<MethodsMetricConfig, Void> {

  @Override
  public void collect (MethodsMetricConfig config, Void context, CompilationUnit unit, Node node) {
    unit.accept (new VoidVisitorAdapter<Void> () {
      @Override
      public void visit (MethodDeclaration type, Void arg) {
        node.getMetrics ().computeIfAbsent ("methods", n -> new AtomicLong ()).addAndGet (1);
        String key;
        switch (type.getAccessSpecifier ()) {
          case PUBLIC -> key = "methods-public";
          case PRIVATE -> key = "methods-private";
          case PROTECTED -> key = "methods-protected";
          default -> key = "methods-package-private";
        }

        node.getMetrics ().computeIfAbsent (key, n -> new AtomicLong ()).addAndGet (1);
        super.visit (type, arg);
      }
    }, null);
  }
}
