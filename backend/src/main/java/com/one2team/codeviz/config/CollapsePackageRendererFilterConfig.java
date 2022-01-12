package com.one2team.codeviz.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

public class CollapsePackageRendererFilterConfig extends RendererFilterConfig {

  public enum Format {
    path_only,
    path_and_package_name,
  }

  @Getter
  @JsonProperty ("name-format")
  private Format nameFormat;
}
