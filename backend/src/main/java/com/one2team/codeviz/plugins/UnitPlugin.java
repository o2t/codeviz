package com.one2team.codeviz.plugins;

import javax.annotation.Priority;
import javax.inject.Inject;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.util.Optional;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.CompilationUnit.Storage;
import com.one2team.codeviz.Node;
import com.one2team.codeviz.Plugin;
import com.one2team.codeviz.PluginContext;
import com.one2team.codeviz.config.PluginConfig;
import com.one2team.codeviz.plugins.UnitPlugin.Config;

import static com.one2team.codeviz.plugins.Priorities.NORMAL;

@Priority (NORMAL)
public class UnitPlugin extends Plugin<Config> {

  public static class Config extends PluginConfig {

  }

  @Inject
  UnitPlugin () {
  }


  @Override
  public void analyze (Config config, PluginContext context, CompilationUnit unit, Node node) {
    long size = Optional.of (unit)
      .flatMap (CompilationUnit::getStorage)
      .map (Storage::getPath)
      .map (path -> {
        try {
          return Files.size (path);
        } catch (IOException e) {
          throw new UncheckedIOException (e);
        }
      })
      .orElse (0L);

    node.getMetrics ().set ("size", size);
  }
}
