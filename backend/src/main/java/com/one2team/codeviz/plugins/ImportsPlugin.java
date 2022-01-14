package com.one2team.codeviz.plugins;

import javax.annotation.Priority;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

import com.one2team.codeviz.Graph;
import com.one2team.codeviz.Node;
import com.one2team.codeviz.Plugin;
import com.one2team.codeviz.PluginContext;
import com.one2team.codeviz.config.PluginConfig;
import com.one2team.codeviz.plugins.ImportsPlugin.Config;

import static com.one2team.codeviz.plugins.Priorities.NORMAL;

@Priority (NORMAL)
public class ImportsPlugin extends Plugin<Config> {

  public static class Config extends PluginConfig {

  }

  @Override
  public void analyze (PluginContext context, Config config) {
    Map<Node, AtomicLong> imported = new HashMap<> ();
    var graph = context.getAttribute (Graph.class);
    graph.getNodes ().values ().forEach (node ->
      imported.put (node, new AtomicLong ()));

    graph.getNodes ().values ().forEach (node ->
      node.getDependencies ().get ().stream ()
        .map (graph.getNodes ()::get)
        .filter (Objects::nonNull)
        .map (dependency -> imported.computeIfAbsent (dependency, d -> new AtomicLong ()))
        .forEach (AtomicLong::incrementAndGet));

    imported.forEach ((node, stat) -> node.getMetrics ().put ("imported-by", stat));
  }
}
