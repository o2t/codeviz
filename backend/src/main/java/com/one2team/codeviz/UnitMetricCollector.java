package com.one2team.codeviz;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.CompilationUnit.Storage;
import com.one2team.codeviz.config.UnitMetricConfig;

public class UnitMetricCollector extends MetricCollector<UnitMetricConfig, Void> {

  @Override
  public void collect (UnitMetricConfig config, Void unused, CompilationUnit unit, Node node) {
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

    node.setSize (size);
    node.getMetrics ().put ("lines", new AtomicLong (size));
  }
}
