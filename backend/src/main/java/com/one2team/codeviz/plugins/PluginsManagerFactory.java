package com.one2team.codeviz.plugins;

import javax.annotation.Priority;
import javax.inject.Inject;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.github.javaparser.ast.CompilationUnit;
import com.one2team.codeviz.Node;
import com.one2team.codeviz.Plugin;
import com.one2team.codeviz.Plugin.Factory;
import com.one2team.codeviz.PluginContext;
import com.one2team.codeviz.PluginsManager;
import com.one2team.codeviz.config.Config;
import com.one2team.codeviz.config.PluginConfig;

import static com.one2team.codeviz.plugins.Priorities.NORMAL;
import static java.util.Comparator.comparing;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;

@Priority (NORMAL)
public class PluginsManagerFactory {

  @Inject
  private Map<Class<? extends PluginConfig>, Plugin.Factory<?, ?>> registry;

  @SuppressWarnings ({ "unchecked", "rawtypes" })
  private record PluginEntry(
    PluginContext context,
    PluginConfig config,
    Plugin plugin
  ) {

    public void analyze1 (CompilationUnit unit, Node node) {
      plugin.analyze1 (context, config, unit, node);
    }

    public void analyze2 (CompilationUnit unit, Node node) {
      plugin.analyze2 (context, config, unit, node);
    }

    public void analyze () {
      plugin.analyze (context, config);
    }
  }

  public PluginsManager create (Config config, PluginContext context) {
    Map<? extends Class<? extends PluginConfig>, PluginConfig> configured = Optional.of (config)
      .map (Config::getPlugins)
      .map (pluginConfigs -> pluginConfigs.stream ()
        .collect (Collectors.toMap (PluginConfig::getClass, Function.identity ())))
      .orElseGet (Collections::emptyMap);

    List<PluginEntry> pluginEntries = registry.entrySet ().stream ()
      .map (entry -> {
        PluginConfig pluginConfig = Optional.of (entry.getKey ())
          .map (configured::get)
          .orElseGet (() -> {
            try {
              return entry.getKey ().getDeclaredConstructor ().newInstance ();
            } catch (Exception e) {
              throw new RuntimeException (e);
            }
          });

        @SuppressWarnings ("rawtypes")
        Factory value = entry.getValue ();

        @SuppressWarnings ("unchecked")
        var plugin = value.create (context, pluginConfig);
        return new PluginEntry (context, pluginConfig, plugin);
      })
      .sorted (comparing (pluginEntry -> ofNullable (pluginEntry.getClass ().getAnnotation (Priority.class))
        .map (Priority::value)
        .orElse (NORMAL)))
      .collect (toList ());

    return new PluginsManager () {
      @Override
      public void analyze1 (CompilationUnit unit, Node node) {
        pluginEntries.forEach (pluginEntry -> pluginEntry.analyze1 (unit, node));
      }

      @Override
      public void analyze2 (CompilationUnit unit, Node node) {
        pluginEntries.forEach (pluginEntry -> pluginEntry.analyze2 (unit, node));
      }

      @Override
      public void analyze () {
        pluginEntries.forEach (PluginEntry::analyze);
      }
    };
  }
}
