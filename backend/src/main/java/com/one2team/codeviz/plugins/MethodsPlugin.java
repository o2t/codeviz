package com.one2team.codeviz.plugins;

import javax.annotation.Priority;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.one2team.codeviz.Node;
import com.one2team.codeviz.Plugin;
import com.one2team.codeviz.PluginContext;
import com.one2team.codeviz.config.PluginConfig;
import com.one2team.codeviz.plugins.MethodsPlugin.Config;

import static com.one2team.codeviz.plugins.Priorities.NORMAL;

@Priority (NORMAL)
public class MethodsPlugin extends Plugin<Config> {

  public static class Config extends PluginConfig {

  }

  @Override
  public void analyze1 (PluginContext context, Config config, CompilationUnit unit, Node node) {
    unit.accept (new VoidVisitorAdapter<Void> () {
      @Override
      public void visit (MethodDeclaration type, Void arg) {
        node.getMetrics ().increment ("methods");
        String key;
        switch (type.getAccessSpecifier ()) {
          case PUBLIC -> key = "methods-public";
          case PRIVATE -> key = "methods-private";
          case PROTECTED -> key = "methods-protected";
          default -> key = "methods-package-private";
        }

        node.getMetrics ().increment (key);
        super.visit (type, arg);
      }
    }, null);
  }
}
