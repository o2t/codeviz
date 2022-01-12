package com.one2team.codeviz.config;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@JsonTypeInfo (use = Id.NAME, include = As.WRAPPER_OBJECT)
@JsonSubTypes ({
  @JsonSubTypes.Type (name = "hierarchical-edge", value = HierarchicalEdgeRendererConfig.class),
  @JsonSubTypes.Type (name = "force-directed", value = ForceDirectedRendererConfig.class),
  @JsonSubTypes.Type (name = "gexf", value = GexfRendererConfig.class),
  @JsonSubTypes.Type (name = "csv", value = CsvRendererConfig.class),
})
public class RendererConfig {

  @Getter
  @JsonProperty ("output")
  private String output;

  @Getter
  @JsonProperty ("filters")
  private List<? extends RendererFilterConfig> filters = new ArrayList<> ();

}
