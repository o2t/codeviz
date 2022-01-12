package com.one2team.codeviz;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

import com.one2team.codeviz.config.ImportedByMetricConfig;

import static java.util.Optional.ofNullable;

public class ImportedMetricByCollector extends MetricCollector<ImportedByMetricConfig, Void> {

  @Override
  public void collect (ImportedByMetricConfig config, Void context, Graph graph) {
    Map<Node, AtomicLong> imported = new HashMap<> ();
    graph.getNodes ().values ().forEach (node ->
      imported.put (node, new AtomicLong ()));

    graph.getNodes ().values ().forEach (node ->
      ofNullable (node.getDependencies ()).ifPresent (dependencies ->
        dependencies.stream ()
          .map (graph.getNodes ()::get)
          .filter (Objects::nonNull)
          .map (dependency -> imported.computeIfAbsent (dependency, d -> new AtomicLong ()))
          .forEach (AtomicLong::incrementAndGet)));

    imported.forEach ((node, stat) -> node.getMetrics ().put ("imported-by", stat));
  }
}
