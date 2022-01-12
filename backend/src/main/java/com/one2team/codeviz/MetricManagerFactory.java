package com.one2team.codeviz;

import javax.inject.Inject;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.github.javaparser.ast.CompilationUnit;
import com.one2team.codeviz.config.Config;
import com.one2team.codeviz.config.MetricConfig;

public class MetricManagerFactory {

  @Inject
  private Map<Class<? extends MetricConfig>, MetricCollector<?, ?>> registry;

  @SuppressWarnings ({ "unchecked", "rawtypes" })
  private static class Metric {

    private final MetricConfig config;

    private final MetricCollector collector;

    private final Object context;

    Metric (MetricConfig config, MetricCollector collector) {
      this.config = config;
      this.collector = collector;
      this.context = collector.newContext (config);
    }

    public void collect (CompilationUnit unit, Node node) {
      collector.collect (config, context, unit, node);
    }

    public void collect (Graph graph) {
      collector.collect (config, context, graph);
    }
  }

  public MetricManager create (Config config) {
    List<Metric> metrics = Optional.of (config)
      .map (Config::getMetrics)
      .map (metricConfigs -> metricConfigs.stream ()
        .map (metricConfig -> new Metric (metricConfig, registry.get (metricConfig.getClass ())))
        .collect (Collectors.toList ()))
      .orElseGet (Collections::emptyList);

    return new MetricManager () {
      @Override
      public void collect (CompilationUnit unit, Node node) {
        metrics.forEach (metric -> metric.collect (unit, node));
      }

      @Override
      public void collect (Graph graph) {
        metrics.forEach (metric -> metric.collect (graph));
      }
    };
  }
}
