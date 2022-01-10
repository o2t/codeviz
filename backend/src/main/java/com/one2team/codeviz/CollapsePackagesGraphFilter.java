package com.one2team.codeviz;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Optional;

import com.one2team.codeviz.Config.CollapsePackageGraphFilterConfig;
import com.one2team.codeviz.Config.GraphFilterConfig;

import static java.util.Optional.ofNullable;

public class CollapsePackagesGraphFilter implements GraphFilter {

  @Override
  public Graph filter (Config config, Graph graph) {
    boolean enabled = ofNullable (config.graphFilters ())
      .map (GraphFilterConfig::collapsePackageGraphFilter)
      .map (CollapsePackageGraphFilterConfig::enabled)
      .orElse (false);

    if (! enabled)
      return graph;

    Map<String, Node> nodes = new HashMap<> ();
    graph.getNodes ().values ().forEach (node -> {
        Node collapsed = nodes
          .computeIfAbsent (extractPackageName (node.getName ()), name -> {
            Node p = new Node (name);
            p.setDependencies (new LinkedHashSet<> ());
            return p;
          });

        ofNullable (node.getDependencies ())
          .ifPresent (d -> d.forEach (e -> {
            collapsed.getDependencies ().add (extractPackageName (e));
            ofNullable (nodes.get (e))
              .map (Node::getSize)
              .ifPresent (size -> collapsed.setSize (collapsed.getSize () + size));
          }));
      }
    );

    Graph collapsed = new Graph ();
    collapsed.setNodes (nodes);
    return collapsed;
  }

  private static String extractPackageName (String className) {
    int i = className.lastIndexOf ('.');
    if (i > 0)
      className = className.substring (0, i);

    return className + "." + className.replaceAll ("\\.", "_");
  }
}
