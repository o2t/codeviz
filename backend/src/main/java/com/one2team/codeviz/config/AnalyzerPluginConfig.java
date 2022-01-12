package com.one2team.codeviz.config;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@JsonTypeInfo (use = Id.NAME, include = As.WRAPPER_OBJECT)
@JsonSubTypes ({
  @JsonSubTypes.Type (name = "imports", value = ImportsAnalyzerPluginConfig.class),
  @JsonSubTypes.Type (name = "methods", value = MethodsAnalyzerPluginConfig.class),
  @JsonSubTypes.Type (name = "inheritance", value = InheritanceAnalyzerPluginConfig.class),
  @JsonSubTypes.Type (name = "unit", value = UnitAnalyzerPluginConfig.class),
})
public class AnalyzerPluginConfig {

}
