package com.one2team.codeviz;

import javax.inject.Inject;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import com.one2team.codeviz.config.RendererConfig;
import com.one2team.codeviz.config.RendererFilterConfig;

import static java.util.Optional.ofNullable;

public abstract class BaseRenderer<CONFIG extends RendererConfig> implements Renderer<CONFIG> {

  @Inject
  private Map<Class<? extends RendererFilterConfig>, RendererFilter<?>> filters;

  @Override
  public void render (CONFIG config, Graph graph) {
    Path output = ofNullable (config.getOutput ())
      .map (Paths::get)
      .orElse (null);

    if (output == null)
      return;

    if (config.getFilters () != null)
      for (RendererFilterConfig filterConfig : config.getFilters ())
        if (filterConfig.isEnabled ())
          graph = applyFilter (graph, filterConfig);

    internalRender (config, graph, output);
  }

  @SuppressWarnings ({ "unchecked", "rawtypes" })
  private Graph applyFilter (Graph graph, RendererFilterConfig filterConfig) {
    RendererFilter filter = filters.get (filterConfig.getClass ());
    return filter.filter (filterConfig, graph);
  }

  protected abstract void internalRender (CONFIG config, Graph graph, Path output);
}
