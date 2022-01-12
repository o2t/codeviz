package com.one2team.codeviz.plugins;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.MapBinder;
import com.one2team.codeviz.AnalyzerPlugin;
import com.one2team.codeviz.UnitAnalyzerPlugin;
import com.one2team.codeviz.config.AnalyzerPluginConfig;
import com.one2team.codeviz.config.ImportsAnalyzerPluginConfig;
import com.one2team.codeviz.config.InheritanceAnalyzerPluginConfig;
import com.one2team.codeviz.config.MethodsAnalyzerPluginConfig;
import com.one2team.codeviz.config.UnitAnalyzerPluginConfig;

import static com.google.inject.multibindings.MapBinder.newMapBinder;

public class PluginsModule implements Module {

  @Override
  public void configure (Binder binder) {
    MapBinder<Class<? extends AnalyzerPluginConfig>, AnalyzerPlugin<?, ?>> plugins = newMapBinder (binder,
      new TypeLiteral<> () {
      },
      new TypeLiteral<> () {
      });

    plugins.addBinding (ImportsAnalyzerPluginConfig.class).to (ImportsAnalyzerPlugin.class);
    plugins.addBinding (MethodsAnalyzerPluginConfig.class).to (MethodsAnalyzerPlugin.class);
    plugins.addBinding (InheritanceAnalyzerPluginConfig.class).to (InheritanceAnalyzerPlugin.class);
    plugins.addBinding (UnitAnalyzerPluginConfig.class).to (UnitAnalyzerPlugin.class);
  }
}
