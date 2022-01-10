package com.one2team.codeviz;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.name.Names;
import com.one2team.codeviz.Config.CollapsePackageGraphFilterConfig;

import static com.google.inject.multibindings.Multibinder.newSetBinder;

public class MainModule implements Module {

  @Override
  public void configure (Binder binder) {
    binder.bind (ObjectMapper.class)
      .annotatedWith (Names.named ("yaml"))
      .toInstance (new ObjectMapper (new YAMLFactory ()));

    binder.bind (ObjectMapper.class)
      .annotatedWith (Names.named ("json"))
      .toInstance (new ObjectMapper ());

    Multibinder<Renderer> renderers = newSetBinder (binder, Renderer.class);
    renderers.addBinding ().to (HierarchicalEdgeRenderer.class);
    renderers.addBinding ().to (ForceDirectedRenderer.class);

    Multibinder<GraphFilter> graphFilters = newSetBinder (binder, GraphFilter.class);
    graphFilters.addBinding ().to (CollapsePackagesGraphFilter.class);
  }
}
