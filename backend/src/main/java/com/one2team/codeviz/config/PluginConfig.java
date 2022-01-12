package com.one2team.codeviz.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver;
import com.one2team.codeviz.utils.JacksonMapTypeResolver;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@JsonTypeInfo (
  use = JsonTypeInfo.Id.CUSTOM,
  include = As.WRAPPER_OBJECT)
@JsonIgnoreProperties (ignoreUnknown = true)
@JsonTypeIdResolver (JacksonMapTypeResolver.class)
public class PluginConfig {

}
