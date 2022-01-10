package com.one2team.codeviz;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ConfigLoader {

  @Inject
  @Named ("yaml")
  private ObjectMapper mapper;

  public Config loadConfig (Path configPath) throws IOException {
    try (Reader input = Files.newBufferedReader (configPath)) {
      return mapper.readValue (input, Config.class);
    }
  }
}
