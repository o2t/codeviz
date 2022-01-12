package com.one2team.codeviz;

import com.github.javaparser.ast.CompilationUnit;
import com.one2team.codeviz.config.PluginConfig;

public class Plugin<CONFIG extends PluginConfig> {

  public interface Factory<CONFIG extends PluginConfig, PLUGIN extends Plugin<CONFIG>> {

    PLUGIN create (PluginContext context, CONFIG config);

  }

  public void analyze (CONFIG config, PluginContext context, CompilationUnit unit, Node node) {
  }

  public void analyze (CONFIG config, PluginContext context) {
  }
}
