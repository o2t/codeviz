package com.one2team.codeviz;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.google.inject.multibindings.MapBinder;
import com.google.inject.name.Names;
import com.one2team.codeviz.config.CollapsePackageRendererFilterConfig;
import com.one2team.codeviz.config.RendererConfig;
import com.one2team.codeviz.config.RendererFilterConfig;
import com.one2team.codeviz.plugins.PluginsModule;
import com.one2team.codeviz.renderers.CollapsePackagesRendererFilter;
import com.one2team.codeviz.renderers.CsvRenderer;
import com.one2team.codeviz.renderers.ForceDirectedRenderer;
import com.one2team.codeviz.renderers.GexfRenderer;
import com.one2team.codeviz.renderers.HierarchicalEdgeRenderer;
import com.one2team.codeviz.renderers.HierarchicalEdgeRenderer.Config;
import com.one2team.codeviz.renderers.RenderersModule;

import static com.google.inject.multibindings.MapBinder.newMapBinder;

public class MainModule implements Module {

  @Override
  public void configure (Binder binder) {
    binder.install (new PluginsModule ());
    binder.install (new RenderersModule ());
    binder.install (new FactoryModuleBuilder ().build (PluginContext.Factory.class));

    binder.bind (ObjectMapper.class)
      .annotatedWith (Names.named ("yaml"))
      .toInstance (new ObjectMapper (new YAMLFactory ())
        .setSerializationInclusion (Include.NON_NULL));

    binder.bind (ObjectMapper.class)
      .annotatedWith (Names.named ("json"))
      .toInstance (new ObjectMapper ()
        .setSerializationInclusion (Include.NON_NULL));

    MapBinder<Class<? extends RendererConfig>, Renderer<?>> renderers = newMapBinder (binder,
      new TypeLiteral<> () {
      },
      new TypeLiteral<> () {
      });

    renderers.addBinding (Config.class).to (HierarchicalEdgeRenderer.class);
    renderers.addBinding (ForceDirectedRenderer.Config.class).to (ForceDirectedRenderer.class);
    renderers.addBinding (GexfRenderer.Config.class).to (GexfRenderer.class);
    renderers.addBinding (CsvRenderer.Config.class).to (CsvRenderer.class);

    MapBinder<Class<? extends RendererFilterConfig>, RendererFilter<?>> graphFilters = newMapBinder (binder,
      new TypeLiteral<> () {
      },
      new TypeLiteral<> () {
      });

    graphFilters.addBinding (CollapsePackageRendererFilterConfig.class).to (CollapsePackagesRendererFilter.class);
  }
}
