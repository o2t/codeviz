package com.one2team.codeviz.config;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

public class CsvRendererConfig extends RendererConfig {

  @Getter
  @JsonProperty ("metrics")
  private List<String> metrics = new ArrayList<> ();

}
