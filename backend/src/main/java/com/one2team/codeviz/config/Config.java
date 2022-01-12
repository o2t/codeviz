package com.one2team.codeviz.config;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class Config {

  @Getter
  @JsonProperty ("src")
  private String src;

  @Getter
  @JsonProperty ("file-include-patterns")
  private List<String> fileIncludePatterns = new ArrayList<> ();

  @Getter
  @JsonProperty ("file-exclude-patterns")
  protected List<String> fileExcludePatterns = new ArrayList<> ();

  @Getter
  @JsonProperty ("import-include-patterns")
  private List<String> importIncludePatterns = new ArrayList<> ();

  @Getter
  @JsonProperty ("renderers")
  private List<RendererConfig> renderers = new ArrayList<> ();

  @Getter
  @JsonProperty ("metrics")
  private List<MetricConfig> metrics = new ArrayList<> ();

}
