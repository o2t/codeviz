package com.one2team.codeviz.renderers;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.one2team.codeviz.RendererModuleBuilder;
import com.one2team.codeviz.config.CollapsePackageRendererFilterConfig;

public class RenderersModule implements Module {

  @Override
  public void configure (Binder binder) {
    binder.install (new RendererModuleBuilder ()
      .registerRenderer ("hierarchical-edge", HierarchicalEdgeRenderer.Config.class, HierarchicalEdgeRenderer.class)
      .registerRenderer ("force-directed", ForceDirectedRenderer.Config.class, ForceDirectedRenderer.class)
      .registerRenderer ("gexf", GexfRenderer.Config.class, GexfRenderer.class)
      .registerRenderer ("csv", CsvRenderer.Config.class, CsvRenderer.class)
      .registerRendererFilter (CollapsePackageRendererFilterConfig.class, CollapsePackagesRendererFilter.class));
  }
}
