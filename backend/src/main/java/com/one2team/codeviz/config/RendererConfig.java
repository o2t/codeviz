package com.one2team.codeviz.config;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver;
import com.one2team.codeviz.utils.JacksonMapTypeResolver;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@JsonTypeInfo (
  use = JsonTypeInfo.Id.CUSTOM,
  include = As.WRAPPER_OBJECT)
@JsonIgnoreProperties (ignoreUnknown = true)
@JsonTypeIdResolver (JacksonMapTypeResolver.class)
public class RendererConfig {

  @Getter
  @JsonProperty ("output")
  private String output;

  @Getter
  @JsonProperty ("filters")
  private List<? extends RendererFilterConfig> filters = new ArrayList<> ();

}
