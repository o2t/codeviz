package com.one2team.codeviz.renderers;

import java.util.HashMap;
import java.util.Map;

import com.one2team.codeviz.Graph;
import com.one2team.codeviz.Node;
import com.one2team.codeviz.RendererFilter;
import com.one2team.codeviz.config.CollapsePackageRendererFilterConfig;
import com.one2team.codeviz.config.CollapsePackageRendererFilterConfig.Format;

import static java.util.Optional.ofNullable;

public class CollapsePackagesRendererFilter implements RendererFilter<CollapsePackageRendererFilterConfig> {

  @Override
  public Graph filter (CollapsePackageRendererFilterConfig config, Graph graph) {
    Map<String, Node> nodes = new HashMap<> ();
    graph.getNodes ().values ().forEach (node -> {
        Node collapsed = nodes.computeIfAbsent (extractPackageName (config, node.getName ()), Node::new);
        node.getDependencies ().get ().forEach (e -> {
          collapsed.getDependencies ().add (extractPackageName (config, e));
          ofNullable (nodes.get (e))
            .map (n -> n.getMetrics ().get ("size"))
            .ifPresent (size -> collapsed.getMetrics ().add ("size", size));
        });
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
