package com.one2team.codeviz.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import lombok.Getter;

@JsonTypeInfo (use = Id.NAME, include = As.WRAPPER_OBJECT)
@JsonSubTypes ({
  @JsonSubTypes.Type (name = "collapse-packages", value = CollapsePackageRendererFilterConfig.class),
})
public class RendererFilterConfig {

  @Getter
  @JsonProperty ("enabled")
  private boolean enabled;

}
