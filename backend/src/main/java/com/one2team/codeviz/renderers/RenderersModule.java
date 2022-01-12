package com.one2team.codeviz.renderers;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.MapBinder;
import com.one2team.codeviz.Renderer;
import com.one2team.codeviz.config.RendererConfig;
import com.one2team.codeviz.utils.JacksonMapTypeResolver;

import static com.google.inject.multibindings.MapBinder.newMapBinder;

public class RenderersModule implements Module {

  @Override
  public void configure (Binder binder) {
    registerRenderer (binder, "hierarchical-edge", HierarchicalEdgeRenderer.Config.class, HierarchicalEdgeRenderer.class);
    registerRenderer (binder, "force-directed", ForceDirectedRenderer.Config.class, ForceDirectedRenderer.class);
    registerRenderer (binder, "gexf", GexfRenderer.Config.class, GexfRenderer.class);
    registerRenderer (binder, "csv", CsvRenderer.Config.class, CsvRenderer.class);
  }

  private <C extends RendererConfig, P extends Renderer<C>>
  void registerRenderer (Binder binder, String rendererName, Class<C> configClass, Class<P> rendererClass) {
    MapBinder<Class<? extends RendererConfig>, Renderer<?>> renderers = newMapBinder (binder,
      new TypeLiteral<> () {
      },
      new TypeLiteral<> () {
      });

    renderers.addBinding (configClass).to (rendererClass);
    JacksonMapTypeResolver.newBuilder (binder, RendererConfig.class)
      .registerType (rendererName, configClass)
      .done ();
  }
}
