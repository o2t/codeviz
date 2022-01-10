package com.one2team.codeviz;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.one2team.codeviz.Config.HierarchicalEdgeRendererConfig;
import com.one2team.codeviz.Config.RendererConfig;

public class HierarchicalEdgeRenderer implements Renderer {

  @Inject
  @Named ("json")
  private ObjectMapper mapper;

  record Entry(
    @JsonProperty ("name") String name,
    @JsonProperty ("size") long size,
    @JsonProperty ("imports") Collection<String> imports ) {

  }

  @Override
  public void render (Config config, Graph graph) {
    Path path = Optional.ofNullable (config)
      .map (Config::renderers)
      .map (RendererConfig::hierarchicalEdgeRendererConfig)
      .map (HierarchicalEdgeRendererConfig::output)
      .map (Paths::get)
      .orElse (null);

    if (path == null)
      return;

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
