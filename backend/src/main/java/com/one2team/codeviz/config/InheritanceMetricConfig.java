package com.one2team.codeviz.config;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

public class InheritanceMetricConfig extends MetricConfig {

  @Getter
  @Setter
  @JsonProperty ("include-patterns")
  private List<String> includePatterns = new ArrayList<> ();

}
