package com.one2team.codeviz;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.MapBinder;
import com.google.inject.name.Names;
import com.one2team.codeviz.config.CollapsePackageRendererFilterConfig;
import com.one2team.codeviz.config.CsvRendererConfig;
import com.one2team.codeviz.config.ForceDirectedRendererConfig;
import com.one2team.codeviz.config.GexfRendererConfig;
import com.one2team.codeviz.config.HierarchicalEdgeRendererConfig;
import com.one2team.codeviz.config.ImportedByMetricConfig;
import com.one2team.codeviz.config.InheritanceMetricConfig;
import com.one2team.codeviz.config.MethodsMetricConfig;
import com.one2team.codeviz.config.MethodsReferencesMetricConfig;
import com.one2team.codeviz.config.MetricConfig;
import com.one2team.codeviz.config.RendererConfig;
import com.one2team.codeviz.config.RendererFilterConfig;

import static com.google.inject.multibindings.MapBinder.newMapBinder;

public class MainModule implements Module {

  @Override
  public void configure (Binder binder) {
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

    renderers.addBinding (HierarchicalEdgeRendererConfig.class).to (HierarchicalEdgeRenderer.class);
    renderers.addBinding (ForceDirectedRendererConfig.class).to (ForceDirectedRenderer.class);
    renderers.addBinding (GexfRendererConfig.class).to (GexfRenderer.class);
    renderers.addBinding (CsvRendererConfig.class).to (CsvRenderer.class);

    MapBinder<Class<? extends RendererFilterConfig>, RendererFilter<?>> graphFilters = newMapBinder (binder,
      new TypeLiteral<> () {
      },
      new TypeLiteral<> () {
      });

    graphFilters.addBinding (CollapsePackageRendererFilterConfig.class).to (CollapsePackagesRendererFilter.class);

    MapBinder<Class<? extends MetricConfig>, MetricCollector<?, ?>> metrics = newMapBinder (binder,
      new TypeLiteral<> () {
      },
      new TypeLiteral<> () {
      });

    metrics.addBinding (ImportedByMetricConfig.class).to (ImportedMetricByCollector.class);
    metrics.addBinding (MethodsMetricConfig.class).to (MethodsMetricCollector.class);
    metrics.addBinding (MethodsReferencesMetricConfig.class).to (MethodsReferencesMetricCollector.class);
    metrics.addBinding (InheritanceMetricConfig.class).to (InheritanceMetricCollector.class);
  }
}
