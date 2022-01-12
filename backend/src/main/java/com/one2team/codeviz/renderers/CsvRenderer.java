package com.one2team.codeviz.renderers;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.one2team.codeviz.Graph;
import com.one2team.codeviz.Renderer;
import com.one2team.codeviz.config.RendererConfig;
import com.one2team.codeviz.renderers.CsvRenderer.Config;
import lombok.Getter;

import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.joining;

public class CsvRenderer extends Renderer<Config> {

  public static class Config extends RendererConfig {

    @Getter
    @JsonProperty ("metrics")
    private List<String> metrics = new ArrayList<> ();

  }

  @Override
  protected void internalRender (Config config, Graph graph, Path output) {
    List<String> metrics = ofNullable (config)
      .map (Config::getMetrics)
      .orElseGet (Collections::emptyList);

    try (PrintWriter writer = new PrintWriter (Files.newBufferedWriter (output))) {
      writer.printf ("name;%s%n", String.join (";", metrics));
      graph.getNodes ().values ().forEach (node ->
        writer.printf ("%s;%s%n",
          node.getName (),
          ofNullable (node.getMetrics ().getMetrics ())
            .map (nodeMetrics -> metrics.stream ()
              .map (nodeMetrics::get)
              .map (atomicLong -> ofNullable (atomicLong)
                .map (AtomicLong::get)
                .orElse (0L))
              .map (String::valueOf)
              .collect (joining (";")))
            .orElse ("")));
    } catch (IOException e) {
      throw new UncheckedIOException (e);
    }
  }
}
