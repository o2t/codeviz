package com.one2team.codeviz;

import javax.inject.Inject;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.google.inject.assistedinject.Assisted;
import com.one2team.codeviz.config.Config;
import com.one2team.codeviz.config.RendererConfig;
import com.one2team.codeviz.plugins.PluginsManagerFactory;
import com.one2team.codeviz.utils.AttributeContainer;
import lombok.Getter;

import static lombok.AccessLevel.PACKAGE;

public class PluginContext extends AttributeContainer {

  interface Factory {

    PluginContext create (Config config);

  }

  @Getter (PACKAGE)
  private final PluginsManager pluginsManager;

  private final Map<Class<? extends RendererConfig>, Renderer<?>> renderers;

  private final Map<String, Node> nodeMap;

  @Getter
  private final Config config;

  @Inject
  PluginContext (
    Map<Class<? extends RendererConfig>, Renderer<?>> renderers,
    PluginsManagerFactory pluginsManagerFactory,
    @Assisted Config config
  ) {
    this.renderers = renderers;
    this.config = config;
    this.pluginsManager = pluginsManagerFactory.create (config, this);
    this.nodeMap = new HashMap<> ();
  }

  Renderer<?> getRenderer (Class<? extends RendererConfig> configClass) {
    return renderers.get (configClass);
  }

  public Node getNode (String name) {
    return nodeMap.computeIfAbsent (name, Node::new);
  }

  public Collection<Node> getNodes () {
    return nodeMap.values ();
  }

  public boolean hasNode (String name) {
    return nodeMap.containsKey (name);
  }
}
