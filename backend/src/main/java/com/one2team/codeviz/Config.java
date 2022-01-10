package com.one2team.codeviz;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Config(
  @JsonProperty ("src") String src,
  @JsonProperty ("file-include-patterns") List<String> fileIncludePatterns,
  @JsonProperty ("file-exclude-patterns") List<String> fileExcludePatterns,
  @JsonProperty ("import-include-patterns") List<String> importIncludePatterns,
  @JsonProperty ("graph-filters") GraphFilterConfig graphFilters,
  @JsonProperty ("renderers") RendererConfig renderers
) {

  public record RendererConfig(
    @JsonProperty ("hierarchical-edge") HierarchicalEdgeRendererConfig hierarchicalEdgeRendererConfig,
    @JsonProperty ("force-directed") ForceDirectedRendererConfig forceDirectedRendererConfig
  ) {

  }

  public record HierarchicalEdgeRendererConfig(
    @JsonProperty ("output") String output
  ) {

  }

  public record ForceDirectedRendererConfig(
    @JsonProperty ("output") String output
  ) {

  }

  public record GraphFilterConfig(
    @JsonProperty ("collapse-packages") CollapsePackageGraphFilterConfig collapsePackageGraphFilter
  ) {

  }

  public record CollapsePackageGraphFilterConfig(
    @JsonProperty ("enabled") boolean enabled
  ) {

  }
}
