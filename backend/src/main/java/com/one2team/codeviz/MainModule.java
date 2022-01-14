package com.one2team.codeviz;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.google.inject.name.Names;
import com.one2team.codeviz.plugins.PluginsModule;
import com.one2team.codeviz.renderers.RenderersModule;

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
  }
}
