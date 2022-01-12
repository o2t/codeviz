package com.one2team.codeviz;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.one2team.codeviz.config.GexfRendererConfig;
import it.uniroma1.dis.wsngroup.gexf4j.core.Mode;
import it.uniroma1.dis.wsngroup.gexf4j.core.Node;
import it.uniroma1.dis.wsngroup.gexf4j.core.impl.GexfImpl;
import it.uniroma1.dis.wsngroup.gexf4j.core.impl.StaxGraphWriter;

import static it.uniroma1.dis.wsngroup.gexf4j.core.EdgeType.DIRECTED;

public class GexfRenderer extends BaseRenderer<GexfRendererConfig> {

  @Override
  protected void internalRender (GexfRendererConfig config, Graph entityGraph, Path output) {
    GexfImpl gexf = new GexfImpl ();
    it.uniroma1.dis.wsngroup.gexf4j.core.Graph graph = gexf.setVisualization (true).getGraph ()
      .setDefaultEdgeType (DIRECTED)
      .setMode (Mode.STATIC);

    Map<String, Node> nodes = new HashMap<> ();
    entityGraph.getNodes ().forEach ((id, node) ->
      nodes.put (id, graph.createNode (id)
        .setLabel (id)
        .setSize (1 + (float) (Math.log10 (node.getMetrics ().get ("imported-by").get ()) / Math.log10 (2)))));

    entityGraph.getNodes ().values ().forEach (node -> {
      Node source = nodes.get (node.getName ());
      node.getDependencies ().stream ()
        .map (nodes::get)
        .filter (Objects::nonNull)
        .forEach (target -> source.connectTo (target).setEdgeType (DIRECTED));
    });

    try (Writer writer = Files.newBufferedWriter (output)) {
      StaxGraphWriter graphWriter = new StaxGraphWriter ();
      graphWriter.writeToStream (gexf, writer, "UTF-8");
    } catch (IOException e) {
      throw new UncheckedIOException (e);
    }
  }
}
