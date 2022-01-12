package com.one2team.codeviz.config;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@JsonTypeInfo (use = Id.NAME, include = As.WRAPPER_OBJECT)
@JsonSubTypes ({
  @JsonSubTypes.Type (name = "imported-by", value = ImportedByMetricConfig.class),
  @JsonSubTypes.Type (name = "methods", value = MethodsMetricConfig.class),
  @JsonSubTypes.Type (name = "inheritance", value = InheritanceMetricConfig.class),
  @JsonSubTypes.Type (name = "unit", value = UnitMetricConfig.class),
})
public class MetricConfig {

}
