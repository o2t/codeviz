package com.one2team.codeviz;

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
import com.one2team.codeviz.config.HierarchicalEdgeRendererConfig;

public class HierarchicalEdgeRenderer extends BaseRenderer<HierarchicalEdgeRendererConfig> {

  @Inject
  @Named ("json")
  private ObjectMapper mapper;

  record Entry(
    @JsonProperty ("name") String name,
    @JsonProperty ("size") long size,
    @JsonProperty ("imports") Collection<String> imports ) {

  }

  @Override
  protected void internalRender (HierarchicalEdgeRendererConfig config, Graph graph, Path path) {
    List<Entry> entries = graph.getNodes ().values ().stream ()
      .map (node -> new Entry (node.getName (), node.getSize (), node.getDependencies ()))
      .collect (Collectors.toList ());

    try (Writer output = Files.newBufferedWriter (path)) {
      mapper.writeValue (output, entries);
    } catch (IOException e) {
      throw new UncheckedIOException (e);
    }
  }
}
