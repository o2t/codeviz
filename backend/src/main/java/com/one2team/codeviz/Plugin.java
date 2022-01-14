package com.one2team.codeviz;

import com.github.javaparser.ast.CompilationUnit;
import com.one2team.codeviz.config.PluginConfig;

public class Plugin<CONFIG extends PluginConfig> {

  public interface Factory<CONFIG extends PluginConfig, PLUGIN extends Plugin<CONFIG>> {

    PLUGIN create (PluginContext context, CONFIG config);

  }

  public void analyze1 (PluginContext context, CONFIG config, CompilationUnit unit, Node node) {
  }

  public void analyze2 (PluginContext context, CONFIG config, CompilationUnit unit, Node node) {
  }

  public void analyze (PluginContext context, CONFIG config) {
  }
}
