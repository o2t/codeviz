package com.one2team.codeviz.plugins;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.one2team.codeviz.PluginModule;

public class PluginsModule implements Module {

  @Override
  public void configure (Binder binder) {
    binder.install (new PluginModule ()
      .registerPlugin ("graph", GraphPlugin.Config.class, GraphPlugin.class)
      .registerPlugin ("imports", ImportsPlugin.Config.class, ImportsPlugin.class)
      .registerPlugin ("methods", MethodsPlugin.Config.class, MethodsPlugin.class)
      .registerPlugin ("inheritance", InheritancePlugin.Config.class, InheritancePlugin.class)
      .registerPlugin ("unit", UnitPlugin.Config.class, UnitPlugin.class));
  }
}
