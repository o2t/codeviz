package com.one2team.codeviz;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.MapBinder;
import com.one2team.codeviz.config.RendererConfig;
import com.one2team.codeviz.config.RendererFilterConfig;
import com.one2team.codeviz.utils.JacksonMapTypeResolver;

import static com.google.inject.multibindings.MapBinder.newMapBinder;

public class RendererModuleBuilder {

  public <C extends RendererConfig, R extends Renderer<C>>
  RendererModule registerRenderer (String rendererName, Class<C> configClass, Class<R> rendererClass) {
    return new RendererModule ().registerRenderer (rendererName, configClass, rendererClass);
  }

  public static class RendererModule implements Module {

    private final List<Consumer<Binder>> rendererRegistry;

    private final List<Consumer<Binder>> rendererFilterRegistry;

    private RendererModule () {
      rendererRegistry = new ArrayList<> ();
      rendererFilterRegistry = new ArrayList<> ();
    }

    @Override
    public void configure (Binder binder) {
      rendererRegistry.forEach (binderConsumer -> binderConsumer.accept (binder));
      rendererFilterRegistry.forEach (binderConsumer -> binderConsumer.accept (binder));
    }

    public <C extends RendererConfig, R extends Renderer<C>>
    RendererModule registerRenderer (String rendererName, Class<C> configClass, Class<R> rendererClass) {
      rendererRegistry.add (binder -> {
        JacksonMapTypeResolver.newBuilder (binder, RendererConfig.class)
          .registerType (rendererName, configClass)
          .done ();

        MapBinder<Class<? extends RendererConfig>, Renderer<?>> renderers = newMapBinder (binder,
          new TypeLiteral<> () {
          },
          new TypeLiteral<> () {
          });

        renderers.addBinding (configClass).to (rendererClass);
      });

      return this;
    }

    public <C extends RendererFilterConfig, F extends RendererFilter<C>>
    RendererModule registerRendererFilter (Class<C> configClass, Class<F> filterClass) {
      rendererFilterRegistry.add (binder -> {
        MapBinder<Class<? extends RendererFilterConfig>, RendererFilter<?>> rendererFilters = newMapBinder (binder,
          new TypeLiteral<> () {
          },
          new TypeLiteral<> () {
          });

        rendererFilters.addBinding (configClass).to (filterClass);
      });

      return this;
    }
  }
}
