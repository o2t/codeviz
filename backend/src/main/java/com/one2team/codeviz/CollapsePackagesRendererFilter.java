package com.one2team.codeviz;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;

import com.one2team.codeviz.config.CollapsePackageRendererFilterConfig;
import com.one2team.codeviz.config.CollapsePackageRendererFilterConfig.Format;

import static java.util.Optional.ofNullable;

public class CollapsePackagesRendererFilter implements RendererFilter<CollapsePackageRendererFilterConfig> {

  @Override
  public Graph filter (CollapsePackageRendererFilterConfig config, Graph graph) {
    Map<String, Node> nodes = new HashMap<> ();
    graph.getNodes ().values ().forEach (node -> {
        Node collapsed = nodes
          .computeIfAbsent (extractPackageName (config, node.getName ()), name -> {
            Node p = new Node (name);
            p.setDependencies (new LinkedHashSet<> ());
            return p;
          });

        ofNullable (node.getDependencies ())
          .ifPresent (d -> d.forEach (e -> {
            collapsed.getDependencies ().add (extractPackageName (config, e));
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

  private static String extractPackageName (CollapsePackageRendererFilterConfig config, String className) {
    int i = className.lastIndexOf ('.');
    if (i > 0)
      className = className.substring (0, i);

    boolean appendName = ofNullable (config.getNameFormat ())
      .map (Format.path_and_package_name::equals)
      .orElse (false);

    if (appendName)
      className = className + "." + className.replaceAll ("\\.", "_");

    return className;
  }
}
