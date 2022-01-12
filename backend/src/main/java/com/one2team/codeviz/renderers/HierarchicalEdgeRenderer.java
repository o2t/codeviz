package com.one2team.codeviz.renderers;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.one2team.codeviz.Graph;
import com.one2team.codeviz.Renderer;
import com.one2team.codeviz.config.RendererConfig;
import com.one2team.codeviz.renderers.HierarchicalEdgeRenderer.Config;

public class HierarchicalEdgeRenderer extends Renderer<Config> {

  public static class Config extends RendererConfig {

  }

  @Inject
  @Named ("json")
  private ObjectMapper mapper;

  record Entry(
    @JsonProperty ("name") String name,
    @JsonProperty ("size") long size,
    @JsonProperty ("imports") Collection<String> imports ) {

  }

  @Override
  protected void internalRender (Config config, Graph graph, Path path) {
    List<Entry> entries = graph.getNodes ().values ().stream ()
      .map (node -> new Entry (node.getName (), node.getMetrics ().get ("size"), node.getDependencies ().get ()))
      .collect (Collectors.toList ());

    try (Writer output = Files.newBufferedWriter (path)) {
      mapper.writeValue (output, entries);
    } catch (IOException e) {
      throw new UncheckedIOException (e);
    }
  }
}
