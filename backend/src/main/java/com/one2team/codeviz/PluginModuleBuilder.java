package com.one2team.codeviz;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import com.google.inject.Binder;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.Provider;
import com.google.inject.TypeLiteral;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.google.inject.multibindings.MapBinder;
import com.google.inject.util.Types;
import com.one2team.codeviz.Plugin.Factory;
import com.one2team.codeviz.config.PluginConfig;
import com.one2team.codeviz.utils.JacksonMapTypeResolver;

import static com.google.inject.multibindings.MapBinder.newMapBinder;

public class PluginModuleBuilder {

  public <C extends PluginConfig, P extends Plugin<C>>
  PluginModule registerPlugin (String pluginName, Class<C> configClass, Class<P> pluginClass) {
    return new PluginModule ().registerPlugin (pluginName, configClass, pluginClass);
  }

  public static class PluginModule implements Module {

    private final List<Consumer<Binder>> registry;

    public PluginModule () {
      registry = new ArrayList<> ();
    }

    @Override
    public void configure (Binder binder) {
      registry.forEach (binderConsumer -> binderConsumer.accept (binder));
    }

    @SuppressWarnings ("unchecked")
    public <C extends PluginConfig, P extends Plugin<C>>
    PluginModule registerPlugin (String pluginName, Class<C> configClass, Class<P> pluginClass) {
      registry.add (binder -> {
        Type factoryType = Types.newParameterizedTypeWithOwner (Plugin.class, Factory.class, configClass, pluginClass);

        binder.install (new FactoryModuleBuilder ().build (TypeLiteral.get (factoryType)));
        MapBinder<Class<? extends PluginConfig>, Factory<?, ?>> plugins = newMapBinder (binder,
          new TypeLiteral<> () {
          },
          new TypeLiteral<> () {
          });

        class RawFactory implements Provider<Factory<? extends PluginConfig, Plugin<?>>> {

          @Inject
          private Injector injector;

          public Factory<? extends PluginConfig, Plugin<?>> get () {
            return (Factory<? extends PluginConfig, Plugin<?>>) injector.getInstance (Key.get (factoryType));
          }
        }
        plugins.addBinding (configClass).toProvider (new RawFactory ());
        JacksonMapTypeResolver.newBuilder (binder, PluginConfig.class)
          .registerType (pluginName, configClass)
          .done ();

      });

      return this;
    }
  }
}
