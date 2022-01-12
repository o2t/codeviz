package com.one2team.codeviz;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.one2team.codeviz.config.ForceDirectedRendererConfig;

import static java.util.Optional.ofNullable;

public class ForceDirectedRenderer extends BaseRenderer<ForceDirectedRendererConfig> {

  record Node(
    @JsonProperty ("id") String id,
    @JsonProperty ("group") int group ) {

  }

  record Link(
    @JsonProperty ("source") String sourceId,
    @JsonProperty ("target") String targetId,
    @JsonProperty ("value") int value ) {

  }

  record ForceGraph(
    @JsonProperty ("nodes") List<Node> nodes,
    @JsonProperty ("links") List<Link> links ) {

  }


  @Inject
  @Named ("json")
  private ObjectMapper mapper;

  @Override
  protected void internalRender (ForceDirectedRendererConfig config, Graph graph, Path output) {
    List<Node> nodes = graph.getNodes ().values ().stream ()
      .map (node -> new Node (node.getName (), 1))
      .collect (Collectors.toList ());

    List<Link> links = graph.getNodes ().values ().stream ()
      .flatMap (node -> ofNullable (node.getDependencies ())
        .map (dependencies -> dependencies.stream ()
          .map (dependency -> new Link (node.getName (), dependency, (int) node.getSize ())))
        .orElseGet (Stream::empty))
      .collect (Collectors.toList ());

    try (Writer writer = Files.newBufferedWriter (output)) {
      mapper.writerWithDefaultPrettyPrinter ().writeValue (writer, new ForceGraph (nodes, links));
    } catch (IOException e) {
      throw new UncheckedIOException (e);
    }
  }
}
